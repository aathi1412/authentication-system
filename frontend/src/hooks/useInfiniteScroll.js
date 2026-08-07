import { useCallback, useEffect, useRef } from "react";

/**
 * Calls `onIntersect` whenever a sentinel element scrolls into view, as long
 * as `hasMore` is true and nothing is already loading. Returns a ref to attach
 * to the sentinel element placed at the end of a list.
 *
 * @param {{ onIntersect: () => void, hasMore: boolean, isLoading: boolean }} args
 */
export function useInfiniteScroll({ onIntersect, hasMore, isLoading }) {
  const observerRef = useRef(null);

  const sentinelRef = useCallback(
    (node) => {
      if (isLoading) return;
      if (observerRef.current) observerRef.current.disconnect();

      observerRef.current = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting && hasMore) {
            onIntersect();
          }
        },
        { rootMargin: "200px" }
      );

      if (node) observerRef.current.observe(node);
    },
    [onIntersect, hasMore, isLoading]
  );

  useEffect(() => {
    return () => observerRef.current?.disconnect();
  }, []);

  return sentinelRef;
}
