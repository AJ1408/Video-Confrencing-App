import React, { useEffect } from 'react';
import VideoPlayer from './video/VideoPlayer';

function App() {
   
  useEffect(() => {
    
  return (
    // This creates a full-height, dark background, and centers everything
    <div className="flex h-screen w-full flex-col items-center justify-center bg-gray-900 text-white">
      <header className="flex flex-col items-center gap-4">
        <h1 className="text-3xl font-bold">My Video Chat App</h1>
        
        {/* This will now show your video in a styled container */}
        <VideoPlayer />

        {/* We can add a second one to test layout */}
        {/* <VideoPlayer /> */}
      </header>
    </div>
  );
}

export default App;