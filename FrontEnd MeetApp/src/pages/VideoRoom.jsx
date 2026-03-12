import React, { useState, useEffect, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import {
  Mic,
  MicOff,
  Video,
  VideoOff,
  PhoneOff,
  Monitor,
  Settings,
  MoreVertical,
  Users,
} from "lucide-react";

const peerConnectionConfig = {
  iceServers: [{ urls: "stun:stun.l.google.com:19302" }],
};

export default function VideoRoom() {
  const { roomId } = useParams();
  const navigate = useNavigate();

  const [localStream, setLocalStream] = useState(null);
  const [remoteStream, setRemoteStream] = useState(null);
  const [status, setStatus] = useState("Initializing...");
  const [isMuted, setIsMuted] = useState(false);
  const [isVideoOff, setIsVideoOff] = useState(false);

  const stompClientRef = useRef(null);
  const peerConnectionRef = useRef(null);
  const localStreamRef = useRef(null);

  const userId = useRef(null);
  if (!userId.current) {
    userId.current = Math.random().toString(36).substring(7);
  }

  useEffect(() => {
    let isActive = true;
    let connectionStarted = false;

    const startCall = async () => {
      if (!isActive || connectionStarted) return;
      connectionStarted = true;

      await setupLocalVideo();
      connectToSocket();
    };

    startCall();

    return () => {
      isActive = false;

      if (stompClientRef.current?.connected) {
        stompClientRef.current.disconnect();
      }

      if (peerConnectionRef.current) {
        peerConnectionRef.current.close();
        peerConnectionRef.current = null;
      }

      if (localStreamRef.current) {
        localStreamRef.current.getTracks().forEach((track) => track.stop());
      }
    };
  }, []);

  const setupLocalVideo = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: true,
        audio: true,
      });

      localStreamRef.current = stream;
      setLocalStream(stream);
      setStatus("Waiting for other user...");
    } catch (err) {
      console.error("Camera access error:", err);
      setStatus("Camera error");
    }
  };

  const connectToSocket = () => {
    const socketFactory = SockJS.default ? SockJS.default : SockJS;
    const socket = new socketFactory("http://localhost:8083/ws");

    const client = Stomp.over(socket);
    client.debug = () => {};

    client.connect({}, () => {
      console.log("Connected to signaling server");
      stompClientRef.current = client;

      client.subscribe(`/topic/room/${roomId}`, (message) => {
        const signal = JSON.parse(message.body);
        handleSignal(signal);
      });

      sendSignal("JOIN");
    });
  };

  const sendSignal = (type, data = null) => {
    if (stompClientRef.current && stompClientRef.current.connected) {
      const message = {
        type,
        sender: userId.current,
        ...(data && { data }),
      };

      stompClientRef.current.send(
        `/app/room/${roomId}`,
        {},
        JSON.stringify(message)
      );

      console.log("Sent signal:", message);
    }
  };

  const handleSignal = async (signal) => {
    console.log("Signal received:", signal);

    if (signal.sender === userId.current) return;

    try {
      if (signal.type.toLowerCase() === "join") {
        console.log("User joined -> creating offer");

        if (peerConnectionRef.current) return;

        peerConnectionRef.current = createPeerConnection();

        const offer = await peerConnectionRef.current.createOffer();
        await peerConnectionRef.current.setLocalDescription(offer);

        sendSignal("offer", offer);
      } else if (signal.type.toLowerCase() === "offer") {
        console.log("Offer received");

        if (!peerConnectionRef.current) {
          peerConnectionRef.current = createPeerConnection();
        }

        await peerConnectionRef.current.setRemoteDescription(
          new RTCSessionDescription(signal.data)
        );

        const answer = await peerConnectionRef.current.createAnswer();
        await peerConnectionRef.current.setLocalDescription(answer);

        sendSignal("answer", answer);
      } else if (signal.type.toLowerCase() === "answer") {
        if (!peerConnectionRef.current) return;

        const state = peerConnectionRef.current.signalingState;

        if (state === "have-local-offer") {
          await peerConnectionRef.current.setRemoteDescription(
            new RTCSessionDescription(signal.data)
          );
          console.log("Answer applied");
        }
      } else if (
        signal.type.toLowerCase() === "ice-candidate" ||
        signal.type.toLowerCase() === "candidate"
      ) {
        if (peerConnectionRef.current && signal.data) {
          await peerConnectionRef.current.addIceCandidate(
            new RTCIceCandidate(signal.data)
          );
        }
      }
    } catch (err) {
      console.error("Signal error:", err);
    }
  };

  const createPeerConnection = () => {
    console.log("Creating PeerConnection");

    const pc = new RTCPeerConnection(peerConnectionConfig);

    peerConnectionRef.current = pc;

    pc.onicecandidate = (event) => {
      if (event.candidate) {
        sendSignal("ice-candidate", event.candidate);
      }
    };

    pc.ontrack = (event) => {
      console.log("Remote track received");

      if (event.streams && event.streams[0]) {
        setRemoteStream(event.streams[0]);
        setStatus("Connected");
      }
    };

    pc.onconnectionstatechange = () => {
      console.log("Connection state:", pc.connectionState);

      if (pc.connectionState === "connected") {
        setStatus("Connected");
      }
    };

    if (localStreamRef.current) {
      localStreamRef.current.getTracks().forEach((track) => {
        console.log("Adding track:", track.kind);
        pc.addTrack(track, localStreamRef.current);
      });
    } else {
      console.warn("No local stream available");
    }

    return pc;
  };

  const toggleMute = () => {
    if (localStreamRef.current) {
      const audioTrack = localStreamRef.current.getAudioTracks()[0];
      if (audioTrack) {
        audioTrack.enabled = !audioTrack.enabled;
        setIsMuted(!audioTrack.enabled);
      }
    }
  };

  const toggleVideo = () => {
    if (localStreamRef.current) {
      const videoTrack = localStreamRef.current.getVideoTracks()[0];
      if (videoTrack) {
        videoTrack.enabled = !videoTrack.enabled;
        setIsVideoOff(!videoTrack.enabled);
      }
    }
  };

  const leaveCall = () => {
    if (localStreamRef.current) {
      localStreamRef.current.getTracks().forEach((track) => track.stop());
    }
    if (peerConnectionRef.current) {
      peerConnectionRef.current.close();
    }
    if (stompClientRef.current?.connected) {
      stompClientRef.current.disconnect();
    }
    navigate("/");
  };

  return (
    <div className="h-screen w-full bg-[#202124] flex flex-col relative overflow-hidden">
      {/* Top Bar */}
      <div className="flex items-center justify-between px-6 py-4 bg-[#202124] z-10">
        <div className="flex items-center gap-4">
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-green-500"></div>
            <span className="text-white text-sm font-medium">{status}</span>
          </div>
          <div className="text-gray-400 text-sm">|</div>
          <span className="text-gray-400 text-sm">{roomId}</span>
        </div>

        <div className="flex items-center gap-2">
          <button className="p-2 hover:bg-[#3c4043] rounded-full transition-colors">
            <Users className="w-5 h-5 text-white" />
          </button>
          <button className="p-2 hover:bg-[#3c4043] rounded-full transition-colors">
            <MoreVertical className="w-5 h-5 text-white" />
          </button>
        </div>
      </div>

      {/* Video Grid */}
      <div className="flex-1 relative p-4">
        {/* Remote Video (Main) */}
        <div className="absolute inset-4 bg-black rounded-lg overflow-hidden">
          {remoteStream ? (
            <video
              ref={(video) => {
                if (video && remoteStream) video.srcObject = remoteStream;
              }}
              autoPlay
              playsInline
              className="w-full h-full object-cover"
            />
          ) : (
            <div className="flex flex-col items-center justify-center h-full text-gray-400">
              <div className="w-24 h-24 bg-gray-700 rounded-full flex items-center justify-center mb-4">
                <Users className="w-12 h-12 text-gray-500" />
              </div>
              <p className="text-lg">Waiting for others to join...</p>
            </div>
          )}
        </div>

        {/* Local Video (Picture-in-Picture) */}
        <div className="absolute bottom-8 right-8 w-64 aspect-video bg-black rounded-lg overflow-hidden border-2 border-gray-700 shadow-2xl z-20">
          {localStream && !isVideoOff ? (
            <video
              ref={(video) => {
                if (video && localStream) video.srcObject = localStream;
              }}
              autoPlay
              muted
              playsInline
              className="w-full h-full object-cover transform -scale-x-100"
            />
          ) : (
            <div className="flex items-center justify-center h-full bg-gray-800">
              <div className="w-16 h-16 bg-gray-700 rounded-full flex items-center justify-center">
                <span className="text-white text-xl font-semibold">You</span>
              </div>
            </div>
          )}
          <div className="absolute bottom-2 left-2 bg-black/70 px-2 py-1 rounded text-white text-xs">
            You
          </div>
        </div>
      </div>

      {/* Bottom Controls */}
      <div className="absolute bottom-8 left-1/2 transform -translate-x-1/2 z-30">
        <div className="bg-[#3c4043] rounded-full px-6 py-4 flex items-center gap-4 shadow-2xl">
          {/* Microphone */}
          <button
            onClick={toggleMute}
            className={`p-4 rounded-full transition-all ${
              isMuted
                ? "bg-red-600 hover:bg-red-700"
                : "hover:bg-[#5f6368] bg-[#3c4043]"
            }`}
          >
            {isMuted ? (
              <MicOff className="w-6 h-6 text-white" />
            ) : (
              <Mic className="w-6 h-6 text-white" />
            )}
          </button>

          {/* Video */}
          <button
            onClick={toggleVideo}
            className={`p-4 rounded-full transition-all ${
              isVideoOff
                ? "bg-red-600 hover:bg-red-700"
                : "hover:bg-[#5f6368] bg-[#3c4043]"
            }`}
          >
            {isVideoOff ? (
              <VideoOff className="w-6 h-6 text-white" />
            ) : (
              <Video className="w-6 h-6 text-white" />
            )}
          </button>

          {/* End Call */}
          <button
            onClick={leaveCall}
            className="p-4 rounded-full bg-red-600 hover:bg-red-700 transition-colors"
          >
            <PhoneOff className="w-6 h-6 text-white" />
          </button>

          {/* Screen Share */}
          {/* <button className="p-4 rounded-full hover:bg-[#5f6368] bg-[#3c4043] transition-colors">
            <Monitor className="w-6 h-6 text-white" />
          </button> */}

          {/* Settings */}
          {/* <button className="p-4 rounded-full hover:bg-[#5f6368] bg-[#3c4043] transition-colors">
            <Settings className="w-6 h-6 text-white" />
          </button> */}
        </div>
      </div>
    </div>
  );
}