import { useState, useEffect } from 'react';
import { ChevronLeft, ChevronRight } from '@mui/icons-material';

const PhotoCarousel = () => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [autoScroll, setAutoScroll] = useState(true);

  // Images from /frontend/images folder
  const photos = [
    {
      id: 1,
      url: '/images/dashboard.jpg',
      title: 'Dashboard',
    },
    {
      id: 2,
      url: '/images/AD1.jpg',
      title: 'Air Defense',
    },
    {
      id: 3,
      url: '/images/hypersonic_missile.jpg',
      title: 'Hypersonic Missile',
    },
    {
      id: 4,
      url: '/images/command.jpg',
      title: 'Command Center',
    },
  ];

  useEffect(() => {
    if (!autoScroll) return;

    const interval = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % photos.length);
    }, 5000); // Change photo every 5 seconds

    return () => clearInterval(interval);
  }, [autoScroll, photos.length]);

  const goToPrevious = () => {
    setAutoScroll(false);
    setCurrentIndex((prev) => (prev - 1 + photos.length) % photos.length);
  };

  const goToNext = () => {
    setAutoScroll(false);
    setCurrentIndex((prev) => (prev + 1) % photos.length);
  };

  const goToSlide = (index) => {
    setAutoScroll(false);
    setCurrentIndex(index);
  };

  return (
    <div className="w-full">
      <div className="relative w-full rounded-lg overflow-hidden bg-gray-900 shadow-lg group">
        {/* Main Carousel */}
        <div className="relative h-96 md:h-[500px]">
          {photos.map((photo, index) => (
            <div
              key={photo.id}
              className={`absolute inset-0 transition-opacity duration-700 ease-in-out ${
                index === currentIndex ? 'opacity-100' : 'opacity-0'
              }`}
            >
              <img
                src={photo.url}
                alt={photo.title}
                className="w-full h-full object-cover"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent" />
              <div className="absolute bottom-0 left-0 right-0 p-6">
                <h3 className="text-white text-xl font-semibold">{photo.title}</h3>
              </div>
            </div>
          ))}

          {/* Navigation Buttons */}
          <button
            onClick={goToPrevious}
            className="absolute left-4 top-1/2 -translate-y-1/2 z-10 bg-white/30 hover:bg-white/50 text-white p-2 rounded-full transition-all duration-200 opacity-0 group-hover:opacity-100"
            aria-label="Previous photo"
          >
            <ChevronLeft sx={{ fontSize: 24 }} />
          </button>

          <button
            onClick={goToNext}
            className="absolute right-4 top-1/2 -translate-y-1/2 z-10 bg-white/30 hover:bg-white/50 text-white p-2 rounded-full transition-all duration-200 opacity-0 group-hover:opacity-100"
            aria-label="Next photo"
          >
            <ChevronRight sx={{ fontSize: 24 }} />
          </button>
        </div>

        {/* Dots Indicator */}
        <div className="absolute bottom-4 left-1/2 -translate-x-1/2 z-20 flex gap-2">
          {photos.map((_, index) => (
            <button
              key={index}
              onClick={() => goToSlide(index)}
              className={`h-2 rounded-full transition-all duration-300 ${
                index === currentIndex
                  ? 'bg-white w-6'
                  : 'bg-white/50 w-2 hover:bg-white/75'
              }`}
              aria-label={`Go to slide ${index + 1}`}
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default PhotoCarousel;
