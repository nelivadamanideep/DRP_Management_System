import { useEffect, useState } from 'react';
import { AddCircleOutline, DeleteOutline } from '@mui/icons-material';
import toast from 'react-hot-toast';
import { api } from '../../app/apiClient';
import { PageHeader, Section, LoadingState } from '../../components/ui/Primitives';
import ImageUploadModal from '../../components/ImageUploadModal';

export default function GalleryPage() {
  const [images, setImages] = useState(null);
  const [showUploadModal, setShowUploadModal] = useState(false);

  const load = async () => {
    try {
      const response = await api.get('/images');
      setImages(response.data);
    } catch (error) {
      console.error('Failed to load images:', error);
      setImages([]);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const deleteImage = async (imageId) => {
    if (!window.confirm('Are you sure you want to delete this image?')) return;

    try {
      await api.delete(`/images/${imageId}`);
      setImages(images.filter(img => img.id !== imageId));
      toast.success('Image deleted successfully');
    } catch (error) {
      toast.error('Failed to delete image');
    }
  };

  return (
    <>
      <PageHeader
        subtitle="Media"
        title="Photo Gallery"
        actions={
          <button
            onClick={() => setShowUploadModal(true)}
            className="btn-primary"
          >
            <AddCircleOutline fontSize="small" />
            Upload Image
          </button>
        }
      />

      <ImageUploadModal
        isOpen={showUploadModal}
        onClose={() => setShowUploadModal(false)}
        onUploadSuccess={(newImage) => {
          setImages([newImage, ...images]);
          load();
        }}
      />

      {!images ? (
        <LoadingState label="Loading gallery..." />
      ) : images.length === 0 ? (
        <Section>
          <div className="text-center py-12">
            <p className="text-ink-500 dark:text-ink-300 mb-4">No images in gallery yet</p>
            <button
              onClick={() => setShowUploadModal(true)}
              className="btn-primary"
            >
              <AddCircleOutline fontSize="small" />
              Upload First Image
            </button>
          </div>
        </Section>
      ) : (
        <Section title={`Gallery (${images.length} images)`}>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {images.map((image) => (
              <div
                key={image.id}
                className="bg-white dark:bg-ink-800 rounded-lg overflow-hidden shadow-md hover:shadow-lg transition-shadow"
              >
                <div className="relative h-48 bg-gray-100 dark:bg-gray-800 overflow-hidden flex items-center justify-center">
                  <img
                    src={image.url}
                    alt={image.title}
                    className="w-auto h-full object-contain"
                  />
                </div>
                <div className="p-4">
                  <h3 className="font-semibold text-sm mb-2">{image.title}</h3>
                  <p className="text-xs text-ink-500 dark:text-ink-300 mb-3">
                    Added: {new Date(image.createdAt).toLocaleDateString()}
                  </p>
                  <button
                    onClick={() => deleteImage(image.id)}
                    className="w-3/4 btn-outline text-xs text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20"
                  >
                    <DeleteOutline fontSize="small" />
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        </Section>
      )}
    </>
  );
}
