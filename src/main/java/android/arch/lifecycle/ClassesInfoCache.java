package android.arch.lifecycle;

import android.arch.lifecycle.Lifecycle.Event;
import android.support.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class ClassesInfoCache {
    private static final int CALL_TYPE_NO_ARG = 0;
    private static final int CALL_TYPE_PROVIDER = 1;
    private static final int CALL_TYPE_PROVIDER_WITH_EVENT = 2;
    static ClassesInfoCache sInstance = new ClassesInfoCache();
    private final Map<Class, CallbackInfo> mCallbackMap = new HashMap();
    private final Map<Class, Boolean> mHasLifecycleMethods = new HashMap();

    static class CallbackInfo {
        final Map<Event, List<MethodReference>> mEventToHandlers = new HashMap();
        final Map<MethodReference, Event> mHandlerToEvent;

        CallbackInfo(Map<MethodReference, Event> map) {
            this.mHandlerToEvent = map;
            for (Entry entry : map.entrySet()) {
                Event event = (Event) entry.getValue();
                List list = (List) this.mEventToHandlers.get(event);
                if (list == null) {
                    list = new ArrayList();
                    this.mEventToHandlers.put(event, list);
                }
                list.add(entry.getKey());
            }
        }

        private static void invokeMethodsForEvent(List<MethodReference> list, LifecycleOwner lifecycleOwner, Event event, Object obj) {
            if (list != null) {
                for (int size = list.size() - 1; size >= 0; size--) {
                    ((MethodReference) list.get(size)).invokeCallback(lifecycleOwner, event, obj);
                }
            }
        }

        /* Access modifiers changed, original: 0000 */
        public void invokeCallbacks(LifecycleOwner lifecycleOwner, Event event, Object obj) {
            invokeMethodsForEvent((List) this.mEventToHandlers.get(event), lifecycleOwner, event, obj);
            invokeMethodsForEvent((List) this.mEventToHandlers.get(Event.ON_ANY), lifecycleOwner, event, obj);
        }
    }

    static class MethodReference {
        final int mCallType;
        final Method mMethod;

        MethodReference(int i, Method method) {
            this.mCallType = i;
            this.mMethod = method;
            this.mMethod.setAccessible(true);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            MethodReference methodReference = (MethodReference) obj;
            return this.mCallType == methodReference.mCallType && this.mMethod.getName().equals(methodReference.mMethod.getName());
        }

        public int hashCode() {
            return (this.mCallType * 31) + this.mMethod.getName().hashCode();
        }

        /* Access modifiers changed, original: 0000 */
        public void invokeCallback(LifecycleOwner lifecycleOwner, Event event, Object obj) {
            try {
                switch (this.mCallType) {
                    case 0:
                        this.mMethod.invoke(obj, new Object[0]);
                        return;
                    case 1:
                        this.mMethod.invoke(obj, new Object[]{lifecycleOwner});
                        return;
                    case 2:
                        this.mMethod.invoke(obj, new Object[]{lifecycleOwner, event});
                        return;
                    default:
                        return;
                }
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Failed to call observer method", e.getCause());
            } catch (IllegalAccessException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    ClassesInfoCache() {
    }

    private CallbackInfo createInfo(Class cls, @Nullable Method[] methodArr) {
        CallbackInfo info;
        Class superclass = cls.getSuperclass();
        HashMap hashMap = new HashMap();
        if (superclass != null) {
            info = getInfo(superclass);
            if (info != null) {
                hashMap.putAll(info.mHandlerToEvent);
            }
        }
        for (Class info2 : cls.getInterfaces()) {
            for (Entry entry : getInfo(info2).mHandlerToEvent.entrySet()) {
                verifyAndPutHandler(hashMap, (MethodReference) entry.getKey(), (Event) entry.getValue(), cls);
            }
        }
        if (methodArr == null) {
            methodArr = getDeclaredMethods(cls);
        }
        int length = methodArr.length;
        boolean z = false;
        int i = 0;
        while (i < length) {
            boolean z2;
            Method method = methodArr[i];
            OnLifecycleEvent onLifecycleEvent = (OnLifecycleEvent) method.getAnnotation(OnLifecycleEvent.class);
            if (onLifecycleEvent == null) {
                z2 = z;
            } else {
                int i2;
                Class[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length <= 0) {
                    i2 = 0;
                } else if (parameterTypes[0].isAssignableFrom(LifecycleOwner.class)) {
                    i2 = 1;
                } else {
                    throw new IllegalArgumentException("invalid parameter type. Must be one and instanceof LifecycleOwner");
                }
                Event value = onLifecycleEvent.value();
                if (parameterTypes.length > 1) {
                    if (!parameterTypes[1].isAssignableFrom(Event.class)) {
                        throw new IllegalArgumentException("invalid parameter type. second arg must be an event");
                    } else if (value == Event.ON_ANY) {
                        i2 = 2;
                    } else {
                        throw new IllegalArgumentException("Second arg is supported only for ON_ANY value");
                    }
                }
                if (parameterTypes.length <= 2) {
                    verifyAndPutHandler(hashMap, new MethodReference(i2, method), value, cls);
                    z2 = true;
                } else {
                    throw new IllegalArgumentException("cannot have more than 2 params");
                }
            }
            i++;
            z = z2;
        }
        info = new CallbackInfo(hashMap);
        this.mCallbackMap.put(cls, info);
        this.mHasLifecycleMethods.put(cls, Boolean.valueOf(z));
        return info;
    }

    private Method[] getDeclaredMethods(Class cls) {
        try {
            return cls.getDeclaredMethods();
        } catch (NoClassDefFoundError e) {
            throw new IllegalArgumentException("The observer class has some methods that use newer APIs which are not available in the current OS version. Lifecycles cannot access even other methods so you should make sure that your observer classes only access framework classes that are available in your min API level OR use lifecycle:compiler annotation processor.", e);
        }
    }

    private void verifyAndPutHandler(Map<MethodReference, Event> map, MethodReference methodReference, Event event, Class cls) {
        Event event2 = (Event) map.get(methodReference);
        if (event2 != null && event != event2) {
            Method method = methodReference.mMethod;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Method ");
            stringBuilder.append(method.getName());
            stringBuilder.append(" in ");
            stringBuilder.append(cls.getName());
            stringBuilder.append(" already declared with different @OnLifecycleEvent value: previous value ");
            stringBuilder.append(event2);
            stringBuilder.append(", new value ");
            stringBuilder.append(event);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (event2 == null) {
            map.put(methodReference, event);
        }
    }

    /* Access modifiers changed, original: 0000 */
    public CallbackInfo getInfo(Class cls) {
        CallbackInfo callbackInfo = (CallbackInfo) this.mCallbackMap.get(cls);
        return callbackInfo != null ? callbackInfo : createInfo(cls, null);
    }

    /* Access modifiers changed, original: 0000 */
    public boolean hasLifecycleMethods(Class cls) {
        if (this.mHasLifecycleMethods.containsKey(cls)) {
            return ((Boolean) this.mHasLifecycleMethods.get(cls)).booleanValue();
        }
        Method[] declaredMethods = getDeclaredMethods(cls);
        for (Method annotation : declaredMethods) {
            if (((OnLifecycleEvent) annotation.getAnnotation(OnLifecycleEvent.class)) != null) {
                createInfo(cls, declaredMethods);
                return true;
            }
        }
        this.mHasLifecycleMethods.put(cls, Boolean.valueOf(false));
        return false;
    }
}
