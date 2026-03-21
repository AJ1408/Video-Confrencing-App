import React, { useEffect, useRef } from 'react';

const VideoPlayer = () => {
  const videoRef = useRef(null);

  const getVideoStream = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: true,
        audio: true,        // get access of audio and video 
      });

      if (videoRef.current) {
        videoRef.current.srcObject = stream;
      }
    } catch (err) {
      console.error("Error accessing media devices.", err);
    }
  };

  useEffect(() => {
    getVideoStream();
  }, []);

  return (
    // You can adjust the width/height classes as needed
    <div className="bg-gray-800 p-2 rounded-lg shadow-xl">
      <video
        ref={videoRef}
        autoPlay
        playsInline
        muted
        className="w-96 rounded-md" // You can change w-96 (384px) to w-full or w-[600px]
      />
    </div>
  );
};

export default VideoPlayer;