import { useEffect } from 'react';
import { useSelector } from 'react-redux';

/**
 * Applies the current theme (`light` / `dark`) to the <html> element.
 * Kept as a component (not a hook) so it can sit at the top of the app tree.
 */
export function ThemeBoot() {
  const mode = useSelector((s) => s.theme.mode);
  useEffect(() => {
    const root = document.documentElement;
    if (mode === 'dark') {
      root.classList.add('dark');
      document.body.classList.add('dark');
    } else {
      root.classList.remove('dark');
      document.body.classList.remove('dark');
    }
  }, [mode]);
  return null;
}
