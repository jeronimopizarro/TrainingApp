import React, { useEffect } from 'react';
import { X } from 'lucide-react';

interface VideoModalProps {
  isOpen: boolean;
  onClose: () => void;
  videoUrl: string;
  title: string;
}

/**
 * VideoModal: Reproductor minimalista con Backdrop Blur.
 * Solo carga el contenido multimedia cuando está abierto para optimizar rendimiento.
 */
export const VideoModal: React.FC<VideoModalProps> = ({ isOpen, onClose, videoUrl, title }) => {
  if (!isOpen || !videoUrl) return null;

  // Cerrar con la tecla Escape
  useEffect(() => {
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', handleEsc);
    return () => window.removeEventListener('keydown', handleEsc);
  }, [onClose]);

  // Detectar si es un link de YouTube para convertirlo a Embed
  const getEmbedUrl = (url: string) => {
    if (url.includes('youtube.com/watch?v=')) {
      return url.replace('watch?v=', 'embed/');
    }
    if (url.includes('youtu.be/')) {
      return url.replace('youtu.be/', 'youtube.com/embed/');
    }
    return url;
  };

  const isYouTube = videoUrl.includes('youtube.com') || videoUrl.includes('youtu.be');

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 md:p-10 animate-in fade-in duration-300">
      {/* Backdrop con Blur intenso */}
      <div 
        className="absolute inset-0 bg-black/80 backdrop-blur-xl" 
        onClick={onClose}
      />
      
      {/* Contenedor del Video */}
      <div className="relative w-full max-w-5xl aspect-video bg-black rounded-[2rem] overflow-hidden shadow-2xl border border-white/5 flex flex-col scale-in duration-500">
        {/* Header del Modal */}
        <div className="absolute top-0 left-0 right-0 p-6 bg-gradient-to-b from-black/80 to-transparent z-10 flex justify-between items-center">
          <h3 className="text-white font-display font-black uppercase italic tracking-widest text-lg drop-shadow-md">
            {title}
          </h3>
          <button 
            onClick={onClose}
            className="p-3 bg-white/10 hover:bg-white/20 rounded-full text-white transition-all backdrop-blur-md"
          >
            <X size={24} />
          </button>
        </div>

        {/* Reproductor */}
        <div className="flex-1 w-full h-full">
          {isYouTube ? (
            <iframe
              src={`${getEmbedUrl(videoUrl)}?autoplay=1`}
              title={title}
              className="w-full h-full border-0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
            />
          ) : (
            <video 
              src={videoUrl} 
              className="w-full h-full object-contain"
              controls 
              autoPlay
            />
          )}
        </div>
      </div>
    </div>
  );
};
