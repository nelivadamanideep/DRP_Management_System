import { useState } from 'react';
import { CloudUpload, X } from '@mui/icons-material';
import toast from 'react-hot-toast';
import { api } from '../app/apiClient';

export default function ImageUploadModal({ isOpen, onClose, onUploadSuccess }) {
  const [file, setFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [title, setTitle] = useState('');

  const handleFileSelect = (e) => {
    const selectedFile = e.target.files?.[0];
    if (selectedFile) {
      if (!selectedFile.type.startsWith('image/')) {
        toast.error('Please select an image file');
        return;
      }
      if (selectedFile.size > 5 * 1024 * 1024) { // 5MB limit
        toast.error('Image size must be less than 5MB');
        return;
      }
      setFile(selectedFile);
      const reader = new FileReader();
      reader.onload = (e) => setPreview(e.target.result);
      reader.readAsDataURL(selectedFile);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file || !title) {
      toast.error('Please select an image and enter a title');
      return;
    }

    setUploading(true);
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('title', title);

      // Upload to backend - it will save to the images folder
      const response = await api.post('/images/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });

      toast.success('Image uploaded successfully');
      onUploadSuccess(response.data);
      resetForm();
      onClose();
    } catch (error) {
      // Fallback: show error message from backend or generic message
      const errorMessage = error.response?.data?.message || 'Failed to upload image';
      toast.error(errorMessage);
      console.error(error);
    } finally {
      setUploading(false);
    }
  };

  const resetForm = () => {
    setFile(null);
    setPreview(null);
    setTitle('');
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-white dark:bg-ink-900 rounded-lg p-6 max-w-md w-full mx-4">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-lg font-semibold">Upload Gallery Image</h2>
          <button onClick={onClose} className="text-ink-500 hover:text-ink-700">
            <X />
          </button>
        </div>

        <form onSubmit={handleUpload} className="space-y-4">
          {/* Image Preview */}
          {preview ? (
            <div className="relative w-full h-48 bg-gray-100 rounded-lg overflow-hidden">
              <img src={preview} alt="Preview" className="w-full h-full object-cover" />
              <button
                type="button"
                onClick={() => {
                  setFile(null);
                  setPreview(null);
                }}
                className="absolute top-2 right-2 bg-red-600 text-white p-1 rounded-full hover:bg-red-700"
              >
                <X fontSize="small" />
              </button>
            </div>
          ) : (
            <label className="border-2 border-dashed border-ink-300 dark:border-ink-600 rounded-lg p-8 text-center cursor-pointer hover:border-accent transition-colors">
              <CloudUpload className="mx-auto mb-2 text-ink-400" fontSize="large" />
              <input
                type="file"
                accept="image/*"
                onChange={handleFileSelect}
                className="hidden"
                required
              />
              <p className="text-sm text-ink-600 dark:text-ink-300">
                Click to select or drag & drop
              </p>
              <p className="text-xs text-ink-500 mt-1">JPG, PNG, GIF (max 5MB)</p>
            </label>
          )}

          {/* Title Input */}
          <div>
            <label className="block text-sm font-medium mb-2">Image Title</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g., Dashboard View"
              className="field-input"
              required
            />
          </div>

          {/* Buttons */}
          <div className="flex gap-2 justify-end">
            <button
              type="button"
              onClick={onClose}
              className="btn-outline"
              disabled={uploading}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn-primary"
              disabled={uploading || !file}
            >
              {uploading ? 'Uploading...' : 'Upload'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
