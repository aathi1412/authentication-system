import { useCallback, useEffect, useRef, useState } from "react";

import { accountApi } from "@/features/account/services/accountApi";

const PAGE_SIZE = 10;

/**
 * Drives the Activity Logs page: paginated fetch, appended on scroll, and
 * reset back to page 0 whenever the search term or category filter changes.
 *
 * @param {{ search: string, category: string }} filters
 */
export function useActivityLogs({ search, category }) {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [isLoading, setIsLoading] = useState(true);
  const [isFetchingMore, setIsFetchingMore] = useState(false);
  const [error, setError] = useState(null);

  // Guards against a slow, stale request landing after a newer filter change.
  const requestId = useRef(0);

  const fetchPage = useCallback(
    async (pageToLoad, { append }) => {
      const currentRequestId = ++requestId.current;
      append ? setIsFetchingMore(true) : setIsLoading(true);
      setError(null);

      try {
        const { data } = await accountApi.getActivityLogs({
          page: pageToLoad,
          size: PAGE_SIZE,
          search,
          category,
        });

        if (currentRequestId !== requestId.current) return; // stale response

        const content = Array.isArray(data) ? data : data.content;
        const more = Array.isArray(data) ? content.length === PAGE_SIZE : data.hasMore;

        setItems((prev) => (append ? [...prev, ...content] : content));
        setHasMore(Boolean(more));
        setPage(pageToLoad);
      } catch (err) {
        if (currentRequestId !== requestId.current) return;
        setError(err);
      } finally {
        if (currentRequestId !== requestId.current) return;
        append ? setIsFetchingMore(false) : setIsLoading(false);
      }
    },
    [search, category]
  );

  // Reset and reload from page 0 whenever filters change.
  useEffect(() => {
    fetchPage(0, { append: false });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [search, category]);

  const loadMore = useCallback(() => {
    if (isFetchingMore || isLoading || !hasMore) return;
    fetchPage(page + 1, { append: true });
  }, [fetchPage, page, hasMore, isFetchingMore, isLoading]);

  return { items, isLoading, isFetchingMore, hasMore, error, loadMore };
}
