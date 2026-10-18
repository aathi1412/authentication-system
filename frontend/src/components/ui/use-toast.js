import * as React from "react";

const TOAST_LIMIT = 3;
const TOAST_REMOVE_DELAY = 5000;

const actionTypes = {
  ADD_TOAST: "ADD_TOAST",
  UPDATE_TOAST: "UPDATE_TOAST",
  DISMISS_TOAST: "DISMISS_TOAST",
  REMOVE_TOAST: "REMOVE_TOAST",
};

let count = 0;
function genId() {
  count = (count + 1) % Number.MAX_SAFE_INTEGER;
  return count.toString();
}

const toastTimeouts = new Map();

function addToRemoveQueue(toastId) {
  if (toastTimeouts.has(toastId)) return;

  const timeout = setTimeout(() => {
    toastTimeouts.delete(toastId);
    dispatch({ type: actionTypes.REMOVE_TOAST, toastId });
  }, TOAST_REMOVE_DELAY);

  toastTimeouts.set(toastId, timeout);
}

function reducer(state, action) {
  switch (action.type) {
    case actionTypes.ADD_TOAST:
      return {
        ...state,
        toasts: [action.toast, ...state.toasts].slice(0, TOAST_LIMIT),
      };

    case actionTypes.UPDATE_TOAST:
      return {
        ...state,
        toasts: state.toasts.map((t) =>
          t.id === action.toast.id ? { ...t, ...action.toast } : t
        ),
      };

    case actionTypes.DISMISS_TOAST: {
      const { toastId } = action;

      if (toastId) {
        addToRemoveQueue(toastId);
      } else {
        state.toasts.forEach((t) => addToRemoveQueue(t.id));
      }

      return {
        ...state,
        toasts: state.toasts.map((t) =>
          t.id === toastId || toastId === undefined
            ? { ...t, open: false }
            : t
        ),
      };
    }

    case actionTypes.REMOVE_TOAST:
      if (action.toastId === undefined) {
        return { ...state, toasts: [] };
      }
      return {
        ...state,
        toasts: state.toasts.filter((t) => t.id !== action.toastId),
      };

    default:
      return state;
  }
}

const listeners = [];
let memoryState = { toasts: [] };

function dispatch(action) {
  memoryState = reducer(memoryState, action);
  listeners.forEach((listener) => listener(memoryState));
}

/**
 * Fire-and-forget toast trigger. Usable outside React components (e.g. axios interceptors).
 * @param {{title?: string, description?: string, variant?: 'default'|'success'|'destructive', duration?: number}} props
 */
function toast({ ...props }) {
  const id = genId();

  const update = (props) =>
    dispatch({ type: actionTypes.UPDATE_TOAST, toast: { ...props, id } });
  const dismiss = () => dispatch({ type: actionTypes.DISMISS_TOAST, toastId: id });

  dispatch({
    type: actionTypes.ADD_TOAST,
    toast: {
      ...props,
      id,
      open: true,
      onOpenChange: (open) => {
        if (!open) dismiss();
      },
    },
  });

  return { id, dismiss, update };
}

toast.success = (title, description) => {
  return toast({
    title: typeof title === "string" ? title : (title?.title || "Success"),
    description: typeof title === "object" ? title.description : description,
    variant: "default",
  });
};

toast.error = (title, description) => {
  return toast({
    title: typeof title === "string" ? title : (title?.title || "Error"),
    description: typeof title === "object" ? title.description : description,
    variant: "destructive",
  });
};

toast.promise = async (promise, { loading, success, error } = {}) => {
  const { id, update, dismiss } = toast({
    title: typeof loading === "string" ? loading : "Loading...",
  });
  try {
    const result = await promise;
    const successMsg = typeof success === "function" ? success(result) : success;
    if (successMsg) {
      update({
        id,
        title: typeof successMsg === "string" ? successMsg : (successMsg?.title || "Success"),
        description: typeof successMsg === "object" ? successMsg.description : undefined,
        variant: "default",
      });
    } else {
      dismiss();
    }
    return result;
  } catch (err) {
    const errorMsg = typeof error === "function" ? error(err) : error;
    update({
      id,
      title: typeof errorMsg === "string" ? errorMsg : (errorMsg?.title || "Error"),
      description: typeof errorMsg === "object" ? errorMsg.description : (err.response?.data?.message || err.message),
      variant: "destructive",
    });
    throw err;
  }
};

function useToast() {
  const [state, setState] = React.useState(memoryState);

  React.useEffect(() => {
    listeners.push(setState);
    return () => {
      const index = listeners.indexOf(setState);
      if (index > -1) listeners.splice(index, 1);
    };
  }, []);

  return {
    ...state,
    toast,
    dismiss: (toastId) => dispatch({ type: actionTypes.DISMISS_TOAST, toastId }),
  };
}

export { useToast, toast };
