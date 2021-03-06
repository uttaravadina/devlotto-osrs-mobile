package android.support.v4.app;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class FragmentTransition {
    private static final int[] INVERSE_OPS = new int[]{0, 3, 0, 1, 5, 4, 7, 6, 9, 8};
    private static final FragmentTransitionImpl PLATFORM_IMPL = (VERSION.SDK_INT >= 21 ? new FragmentTransitionCompat21() : null);
    private static final FragmentTransitionImpl SUPPORT_IMPL = resolveSupportImpl();

    static class FragmentContainerTransition {
        public Fragment firstOut;
        public boolean firstOutIsPop;
        public BackStackRecord firstOutTransaction;
        public Fragment lastIn;
        public boolean lastInIsPop;
        public BackStackRecord lastInTransaction;

        FragmentContainerTransition() {
        }
    }

    private FragmentTransition() {
    }

    private static void addSharedElementsWithMatchingNames(ArrayList<View> arrayList, ArrayMap<String, View> arrayMap, Collection<String> collection) {
        for (int size = arrayMap.size() - 1; size >= 0; size--) {
            View view = (View) arrayMap.valueAt(size);
            if (collection.contains(ViewCompat.getTransitionName(view))) {
                arrayList.add(view);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:83:0x00da  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0026  */
    /* JADX WARNING: Missing block: B:44:0x0080, code skipped:
            if (r1.mAdded != false) goto L_0x0082;
     */
    /* JADX WARNING: Missing block: B:45:0x0082, code skipped:
            r0 = true;
     */
    /* JADX WARNING: Missing block: B:55:0x0096, code skipped:
            r0 = 1;
     */
    /* JADX WARNING: Missing block: B:56:0x0097, code skipped:
            r5 = 0;
            r6 = 1;
            r7 = r0;
            r4 = 0;
     */
    /* JADX WARNING: Missing block: B:61:0x00a4, code skipped:
            r0 = 0;
     */
    /* JADX WARNING: Missing block: B:80:0x00d4, code skipped:
            if (r1.mHidden == false) goto L_0x0082;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void addToFirstInLastOut(BackStackRecord backStackRecord, Op op, SparseArray<FragmentContainerTransition> sparseArray, boolean z, boolean z2) {
        Fragment fragment = op.fragment;
        if (fragment != null) {
            int i = fragment.mContainerId;
            if (i != 0) {
                boolean z3;
                int i2;
                int i3 = z ? INVERSE_OPS[op.cmd] : op.cmd;
                if (i3 != 1) {
                    switch (i3) {
                        case 3:
                        case 6:
                            if (!z2) {
                                break;
                            }
                            break;
                        case 4:
                            if (!z2) {
                                break;
                            }
                            break;
                        case 5:
                            if (z2) {
                                if (fragment.mHiddenChanged) {
                                    if (!fragment.mHidden) {
                                        break;
                                    }
                                }
                            }
                            z3 = fragment.mHidden;
                            break;
                            break;
                        case 7:
                            break;
                        default:
                            z3 = false;
                            i2 = 0;
                            break;
                    }
                }
                int i4;
                int i5;
                int i6;
                FragmentContainerTransition fragmentContainerTransition;
                FragmentContainerTransition ensureContainer;
                FragmentManagerImpl fragmentManagerImpl;
                if (z2) {
                    z3 = fragment.mIsNewlyAdded;
                    i2 = 1;
                    i4 = z3;
                    i5 = 0;
                    i6 = 0;
                    fragmentContainerTransition = (FragmentContainerTransition) sparseArray.get(i);
                    if (i4 == 0) {
                        ensureContainer = ensureContainer(fragmentContainerTransition, sparseArray, i);
                        ensureContainer.lastIn = fragment;
                        ensureContainer.lastInIsPop = z;
                        ensureContainer.lastInTransaction = backStackRecord;
                    } else {
                        ensureContainer = fragmentContainerTransition;
                    }
                    if (!(z2 || r4 == 0)) {
                        if (ensureContainer != null && ensureContainer.firstOut == fragment) {
                            ensureContainer.firstOut = null;
                        }
                        fragmentManagerImpl = backStackRecord.mManager;
                        if (fragment.mState < 1 && fragmentManagerImpl.mCurState >= 1 && !backStackRecord.mReorderingAllowed) {
                            fragmentManagerImpl.makeActive(fragment);
                            fragmentManagerImpl.moveToState(fragment, 1, 0, 0, false);
                        }
                    }
                    if (i6 == 0 && (ensureContainer == null || ensureContainer.firstOut == null)) {
                        fragmentContainerTransition = ensureContainer(ensureContainer, sparseArray, i);
                        fragmentContainerTransition.firstOut = fragment;
                        fragmentContainerTransition.firstOutIsPop = z;
                        fragmentContainerTransition.firstOutTransaction = backStackRecord;
                    } else {
                        fragmentContainerTransition = ensureContainer;
                    }
                    if (!z2 && i5 != 0 && fragmentContainerTransition != null && fragmentContainerTransition.lastIn == fragment) {
                        fragmentContainerTransition.lastIn = null;
                        return;
                    }
                    return;
                }
                if (!fragment.mAdded) {
                }
                z3 = false;
                i2 = 1;
                i4 = z3;
                i5 = 0;
                i6 = 0;
                fragmentContainerTransition = (FragmentContainerTransition) sparseArray.get(i);
                if (i4 == 0) {
                }
                ensureContainer.firstOut = null;
                fragmentManagerImpl = backStackRecord.mManager;
                fragmentManagerImpl.makeActive(fragment);
                fragmentManagerImpl.moveToState(fragment, 1, 0, 0, false);
                if (i6 == 0) {
                }
                fragmentContainerTransition = ensureContainer;
                if (!z2) {
                }
            }
        }
    }

    public static void calculateFragments(BackStackRecord backStackRecord, SparseArray<FragmentContainerTransition> sparseArray, boolean z) {
        int size = backStackRecord.mOps.size();
        for (int i = 0; i < size; i++) {
            addToFirstInLastOut(backStackRecord, (Op) backStackRecord.mOps.get(i), sparseArray, false, z);
        }
    }

    private static ArrayMap<String, String> calculateNameOverrides(int i, ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2, int i2, int i3) {
        ArrayMap arrayMap = new ArrayMap();
        for (int i4 = i3 - 1; i4 >= i2; i4--) {
            BackStackRecord backStackRecord = (BackStackRecord) arrayList.get(i4);
            if (backStackRecord.interactsWith(i)) {
                boolean booleanValue = ((Boolean) arrayList2.get(i4)).booleanValue();
                if (backStackRecord.mSharedElementSourceNames != null) {
                    ArrayList arrayList3;
                    ArrayList arrayList4;
                    int size = backStackRecord.mSharedElementSourceNames.size();
                    if (booleanValue) {
                        arrayList3 = backStackRecord.mSharedElementSourceNames;
                        arrayList4 = backStackRecord.mSharedElementTargetNames;
                    } else {
                        ArrayList arrayList5 = backStackRecord.mSharedElementSourceNames;
                        arrayList3 = backStackRecord.mSharedElementTargetNames;
                        arrayList4 = arrayList5;
                    }
                    for (int i5 = 0; i5 < size; i5++) {
                        String str = (String) arrayList4.get(i5);
                        String str2 = (String) arrayList3.get(i5);
                        String str3 = (String) arrayMap.remove(str2);
                        if (str3 != null) {
                            arrayMap.put(str, str3);
                        } else {
                            arrayMap.put(str, str2);
                        }
                    }
                }
            }
        }
        return arrayMap;
    }

    public static void calculatePopFragments(BackStackRecord backStackRecord, SparseArray<FragmentContainerTransition> sparseArray, boolean z) {
        if (backStackRecord.mManager.mContainer.onHasView()) {
            for (int size = backStackRecord.mOps.size() - 1; size >= 0; size--) {
                addToFirstInLastOut(backStackRecord, (Op) backStackRecord.mOps.get(size), sparseArray, true, z);
            }
        }
    }

    static void callSharedElementStartEnd(Fragment fragment, Fragment fragment2, boolean z, ArrayMap<String, View> arrayMap, boolean z2) {
        SharedElementCallback enterTransitionCallback = z ? fragment2.getEnterTransitionCallback() : fragment.getEnterTransitionCallback();
        if (enterTransitionCallback != null) {
            int i;
            int i2;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            if (arrayMap == null) {
                i = 0;
                i2 = 0;
            } else {
                i2 = arrayMap.size();
                i = 0;
            }
            while (i < i2) {
                arrayList2.add(arrayMap.keyAt(i));
                arrayList.add(arrayMap.valueAt(i));
                i++;
            }
            if (z2) {
                enterTransitionCallback.onSharedElementStart(arrayList2, arrayList, null);
            } else {
                enterTransitionCallback.onSharedElementEnd(arrayList2, arrayList, null);
            }
        }
    }

    private static boolean canHandleAll(FragmentTransitionImpl fragmentTransitionImpl, List<Object> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (!fragmentTransitionImpl.canHandle(list.get(i))) {
                return false;
            }
        }
        return true;
    }

    static ArrayMap<String, View> captureInSharedElements(FragmentTransitionImpl fragmentTransitionImpl, ArrayMap<String, String> arrayMap, Object obj, FragmentContainerTransition fragmentContainerTransition) {
        Fragment fragment = fragmentContainerTransition.lastIn;
        View view = fragment.getView();
        if (arrayMap.isEmpty() || obj == null || view == null) {
            arrayMap.clear();
            return null;
        }
        SharedElementCallback exitTransitionCallback;
        Collection collection;
        ArrayMap<String, View> arrayMap2 = new ArrayMap();
        fragmentTransitionImpl.findNamedViews(arrayMap2, view);
        BackStackRecord backStackRecord = fragmentContainerTransition.lastInTransaction;
        if (fragmentContainerTransition.lastInIsPop) {
            exitTransitionCallback = fragment.getExitTransitionCallback();
            collection = backStackRecord.mSharedElementSourceNames;
        } else {
            exitTransitionCallback = fragment.getEnterTransitionCallback();
            Object collection2 = backStackRecord.mSharedElementTargetNames;
        }
        if (collection2 != null) {
            arrayMap2.retainAll(collection2);
            arrayMap2.retainAll(arrayMap.values());
        }
        if (exitTransitionCallback != null) {
            exitTransitionCallback.onMapSharedElements(collection2, arrayMap2);
            for (int size = collection2.size() - 1; size >= 0; size--) {
                String str = (String) collection2.get(size);
                view = (View) arrayMap2.get(str);
                if (view == null) {
                    str = findKeyForValue(arrayMap, str);
                    if (str != null) {
                        arrayMap.remove(str);
                    }
                } else if (!str.equals(ViewCompat.getTransitionName(view))) {
                    str = findKeyForValue(arrayMap, str);
                    if (str != null) {
                        arrayMap.put(str, ViewCompat.getTransitionName(view));
                    }
                }
            }
        } else {
            retainValues(arrayMap, arrayMap2);
        }
        return arrayMap2;
    }

    private static ArrayMap<String, View> captureOutSharedElements(FragmentTransitionImpl fragmentTransitionImpl, ArrayMap<String, String> arrayMap, Object obj, FragmentContainerTransition fragmentContainerTransition) {
        if (arrayMap.isEmpty() || obj == null) {
            arrayMap.clear();
            return null;
        }
        SharedElementCallback enterTransitionCallback;
        Collection collection;
        Fragment fragment = fragmentContainerTransition.firstOut;
        ArrayMap<String, View> arrayMap2 = new ArrayMap();
        fragmentTransitionImpl.findNamedViews(arrayMap2, fragment.getView());
        BackStackRecord backStackRecord = fragmentContainerTransition.firstOutTransaction;
        if (fragmentContainerTransition.firstOutIsPop) {
            enterTransitionCallback = fragment.getEnterTransitionCallback();
            collection = backStackRecord.mSharedElementTargetNames;
        } else {
            enterTransitionCallback = fragment.getExitTransitionCallback();
            Object collection2 = backStackRecord.mSharedElementSourceNames;
        }
        arrayMap2.retainAll(collection2);
        if (enterTransitionCallback != null) {
            enterTransitionCallback.onMapSharedElements(collection2, arrayMap2);
            for (int size = collection2.size() - 1; size >= 0; size--) {
                String str = (String) collection2.get(size);
                View view = (View) arrayMap2.get(str);
                if (view == null) {
                    arrayMap.remove(str);
                } else if (!str.equals(ViewCompat.getTransitionName(view))) {
                    arrayMap.put(ViewCompat.getTransitionName(view), (String) arrayMap.remove(str));
                }
            }
        } else {
            arrayMap.retainAll(arrayMap2.keySet());
        }
        return arrayMap2;
    }

    private static FragmentTransitionImpl chooseImpl(Fragment fragment, Fragment fragment2) {
        Object exitTransition;
        ArrayList arrayList = new ArrayList();
        if (fragment != null) {
            exitTransition = fragment.getExitTransition();
            if (exitTransition != null) {
                arrayList.add(exitTransition);
            }
            exitTransition = fragment.getReturnTransition();
            if (exitTransition != null) {
                arrayList.add(exitTransition);
            }
            exitTransition = fragment.getSharedElementReturnTransition();
            if (exitTransition != null) {
                arrayList.add(exitTransition);
            }
        }
        if (fragment2 != null) {
            exitTransition = fragment2.getEnterTransition();
            if (exitTransition != null) {
                arrayList.add(exitTransition);
            }
            exitTransition = fragment2.getReenterTransition();
            if (exitTransition != null) {
                arrayList.add(exitTransition);
            }
            exitTransition = fragment2.getSharedElementEnterTransition();
            if (exitTransition != null) {
                arrayList.add(exitTransition);
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        if (PLATFORM_IMPL != null && canHandleAll(PLATFORM_IMPL, arrayList)) {
            return PLATFORM_IMPL;
        }
        if (SUPPORT_IMPL != null && canHandleAll(SUPPORT_IMPL, arrayList)) {
            return SUPPORT_IMPL;
        }
        if (PLATFORM_IMPL == null && SUPPORT_IMPL == null) {
            return null;
        }
        throw new IllegalArgumentException("Invalid Transition types");
    }

    static ArrayList<View> configureEnteringExitingViews(FragmentTransitionImpl fragmentTransitionImpl, Object obj, Fragment fragment, ArrayList<View> arrayList, View view) {
        if (obj == null) {
            return null;
        }
        ArrayList arrayList2 = new ArrayList();
        View view2 = fragment.getView();
        if (view2 != null) {
            fragmentTransitionImpl.captureTransitioningViews(arrayList2, view2);
        }
        if (arrayList != null) {
            arrayList2.removeAll(arrayList);
        }
        if (arrayList2.isEmpty()) {
            return arrayList2;
        }
        arrayList2.add(view);
        fragmentTransitionImpl.addTargets(obj, arrayList2);
        return arrayList2;
    }

    private static Object configureSharedElementsOrdered(FragmentTransitionImpl fragmentTransitionImpl, ViewGroup viewGroup, View view, ArrayMap<String, String> arrayMap, FragmentContainerTransition fragmentContainerTransition, ArrayList<View> arrayList, ArrayList<View> arrayList2, Object obj, Object obj2) {
        final Fragment fragment = fragmentContainerTransition.lastIn;
        final Fragment fragment2 = fragmentContainerTransition.firstOut;
        if (fragment == null) {
            return null;
        }
        if (fragment2 == null) {
            return null;
        }
        Object obj3;
        final boolean z = fragmentContainerTransition.lastInIsPop;
        Object sharedElementTransition = arrayMap.isEmpty() ? null : getSharedElementTransition(fragmentTransitionImpl, fragment, fragment2, z);
        ArrayMap captureOutSharedElements = captureOutSharedElements(fragmentTransitionImpl, arrayMap, sharedElementTransition, fragmentContainerTransition);
        if (arrayMap.isEmpty()) {
            obj3 = null;
        } else {
            arrayList.addAll(captureOutSharedElements.values());
            obj3 = sharedElementTransition;
        }
        if (obj == null && obj2 == null && obj3 == null) {
            return null;
        }
        Rect rect;
        callSharedElementStartEnd(fragment, fragment2, z, captureOutSharedElements, true);
        if (obj3 != null) {
            rect = new Rect();
            fragmentTransitionImpl.setSharedElementTargets(obj3, view, arrayList);
            setOutEpicenter(fragmentTransitionImpl, obj3, obj2, captureOutSharedElements, fragmentContainerTransition.firstOutIsPop, fragmentContainerTransition.firstOutTransaction);
            if (obj != null) {
                fragmentTransitionImpl.setEpicenter(obj, rect);
            }
        } else {
            rect = null;
        }
        final FragmentTransitionImpl fragmentTransitionImpl2 = fragmentTransitionImpl;
        final ArrayMap<String, String> arrayMap2 = arrayMap;
        final Object obj4 = obj3;
        final FragmentContainerTransition fragmentContainerTransition2 = fragmentContainerTransition;
        final ArrayList<View> arrayList3 = arrayList2;
        final View view2 = view;
        final ArrayList<View> arrayList4 = arrayList;
        final Object obj5 = obj;
        OneShotPreDrawListener.add(viewGroup, new Runnable() {
            public void run() {
                ArrayMap captureInSharedElements = FragmentTransition.captureInSharedElements(fragmentTransitionImpl2, arrayMap2, obj4, fragmentContainerTransition2);
                if (captureInSharedElements != null) {
                    arrayList3.addAll(captureInSharedElements.values());
                    arrayList3.add(view2);
                }
                FragmentTransition.callSharedElementStartEnd(fragment, fragment2, z, captureInSharedElements, false);
                if (obj4 != null) {
                    fragmentTransitionImpl2.swapSharedElementTargets(obj4, arrayList4, arrayList3);
                    View inEpicenterView = FragmentTransition.getInEpicenterView(captureInSharedElements, fragmentContainerTransition2, obj5, z);
                    if (inEpicenterView != null) {
                        fragmentTransitionImpl2.getBoundsOnScreen(inEpicenterView, rect);
                    }
                }
            }
        });
        return obj3;
    }

    private static Object configureSharedElementsReordered(FragmentTransitionImpl fragmentTransitionImpl, ViewGroup viewGroup, View view, ArrayMap<String, String> arrayMap, FragmentContainerTransition fragmentContainerTransition, ArrayList<View> arrayList, ArrayList<View> arrayList2, Object obj, Object obj2) {
        Fragment fragment = fragmentContainerTransition.lastIn;
        Fragment fragment2 = fragmentContainerTransition.firstOut;
        if (fragment != null) {
            fragment.getView().setVisibility(0);
        }
        if (fragment == null) {
            return null;
        }
        if (fragment2 == null) {
            return null;
        }
        Object obj3;
        boolean z = fragmentContainerTransition.lastInIsPop;
        Object sharedElementTransition = arrayMap.isEmpty() ? null : getSharedElementTransition(fragmentTransitionImpl, fragment, fragment2, z);
        ArrayMap captureOutSharedElements = captureOutSharedElements(fragmentTransitionImpl, arrayMap, sharedElementTransition, fragmentContainerTransition);
        final ArrayMap captureInSharedElements = captureInSharedElements(fragmentTransitionImpl, arrayMap, sharedElementTransition, fragmentContainerTransition);
        if (arrayMap.isEmpty()) {
            if (captureOutSharedElements != null) {
                captureOutSharedElements.clear();
            }
            if (captureInSharedElements != null) {
                captureInSharedElements.clear();
            }
            obj3 = null;
        } else {
            addSharedElementsWithMatchingNames(arrayList, captureOutSharedElements, arrayMap.keySet());
            addSharedElementsWithMatchingNames(arrayList2, captureInSharedElements, arrayMap.values());
            obj3 = sharedElementTransition;
        }
        if (obj == null && obj2 == null && obj3 == null) {
            return null;
        }
        Rect rect;
        View inEpicenterView;
        callSharedElementStartEnd(fragment, fragment2, z, captureOutSharedElements, true);
        if (obj3 != null) {
            arrayList2.add(view);
            fragmentTransitionImpl.setSharedElementTargets(obj3, view, arrayList);
            setOutEpicenter(fragmentTransitionImpl, obj3, obj2, captureOutSharedElements, fragmentContainerTransition.firstOutIsPop, fragmentContainerTransition.firstOutTransaction);
            rect = new Rect();
            inEpicenterView = getInEpicenterView(captureInSharedElements, fragmentContainerTransition, obj, z);
            if (inEpicenterView != null) {
                fragmentTransitionImpl.setEpicenter(obj, rect);
            }
        } else {
            rect = null;
            inEpicenterView = null;
        }
        final Fragment fragment3 = fragment;
        final Fragment fragment4 = fragment2;
        final boolean z2 = z;
        final FragmentTransitionImpl fragmentTransitionImpl2 = fragmentTransitionImpl;
        OneShotPreDrawListener.add(viewGroup, new Runnable() {
            public void run() {
                FragmentTransition.callSharedElementStartEnd(fragment3, fragment4, z2, captureInSharedElements, false);
                if (inEpicenterView != null) {
                    fragmentTransitionImpl2.getBoundsOnScreen(inEpicenterView, rect);
                }
            }
        });
        return obj3;
    }

    private static void configureTransitionsOrdered(FragmentManagerImpl fragmentManagerImpl, int i, FragmentContainerTransition fragmentContainerTransition, View view, ArrayMap<String, String> arrayMap) {
        ViewGroup viewGroup = fragmentManagerImpl.mContainer.onHasView() ? (ViewGroup) fragmentManagerImpl.mContainer.onFindViewById(i) : null;
        if (viewGroup != null) {
            Fragment fragment = fragmentContainerTransition.lastIn;
            Fragment fragment2 = fragmentContainerTransition.firstOut;
            FragmentTransitionImpl chooseImpl = chooseImpl(fragment2, fragment);
            if (chooseImpl != null) {
                boolean z = fragmentContainerTransition.lastInIsPop;
                boolean z2 = fragmentContainerTransition.firstOutIsPop;
                Object enterTransition = getEnterTransition(chooseImpl, fragment, z);
                Object exitTransition = getExitTransition(chooseImpl, fragment2, z2);
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                Object configureSharedElementsOrdered = configureSharedElementsOrdered(chooseImpl, viewGroup, view, arrayMap, fragmentContainerTransition, arrayList, arrayList2, enterTransition, exitTransition);
                if (enterTransition != null || configureSharedElementsOrdered != null || exitTransition != null) {
                    ArrayList configureEnteringExitingViews = configureEnteringExitingViews(chooseImpl, exitTransition, fragment2, arrayList, view);
                    Object obj = (configureEnteringExitingViews == null || configureEnteringExitingViews.isEmpty()) ? null : exitTransition;
                    chooseImpl.addTarget(enterTransition, view);
                    Object mergeTransitions = mergeTransitions(chooseImpl, enterTransition, obj, configureSharedElementsOrdered, fragment, fragmentContainerTransition.lastInIsPop);
                    if (mergeTransitions != null) {
                        ArrayList arrayList3 = new ArrayList();
                        chooseImpl.scheduleRemoveTargets(mergeTransitions, enterTransition, arrayList3, obj, configureEnteringExitingViews, configureSharedElementsOrdered, arrayList2);
                        scheduleTargetChange(chooseImpl, viewGroup, fragment, view, arrayList2, enterTransition, arrayList3, obj, configureEnteringExitingViews);
                        chooseImpl.setNameOverridesOrdered(viewGroup, arrayList2, arrayMap);
                        chooseImpl.beginDelayedTransition(viewGroup, mergeTransitions);
                        chooseImpl.scheduleNameReset(viewGroup, arrayList2, arrayMap);
                    }
                }
            }
        }
    }

    private static void configureTransitionsReordered(FragmentManagerImpl fragmentManagerImpl, int i, FragmentContainerTransition fragmentContainerTransition, View view, ArrayMap<String, String> arrayMap) {
        View view2 = fragmentManagerImpl.mContainer.onHasView() ? (ViewGroup) fragmentManagerImpl.mContainer.onFindViewById(i) : null;
        if (view2 != null) {
            Fragment fragment = fragmentContainerTransition.lastIn;
            Fragment fragment2 = fragmentContainerTransition.firstOut;
            FragmentTransitionImpl chooseImpl = chooseImpl(fragment2, fragment);
            if (chooseImpl != null) {
                boolean z = fragmentContainerTransition.lastInIsPop;
                boolean z2 = fragmentContainerTransition.firstOutIsPop;
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                Object enterTransition = getEnterTransition(chooseImpl, fragment, z);
                Object exitTransition = getExitTransition(chooseImpl, fragment2, z2);
                Object configureSharedElementsReordered = configureSharedElementsReordered(chooseImpl, view2, view, arrayMap, fragmentContainerTransition, arrayList2, arrayList, enterTransition, exitTransition);
                if (enterTransition != null || configureSharedElementsReordered != null || exitTransition != null) {
                    ArrayList configureEnteringExitingViews = configureEnteringExitingViews(chooseImpl, exitTransition, fragment2, arrayList2, view);
                    ArrayList configureEnteringExitingViews2 = configureEnteringExitingViews(chooseImpl, enterTransition, fragment, arrayList, view);
                    setViewVisibility(configureEnteringExitingViews2, 4);
                    Object mergeTransitions = mergeTransitions(chooseImpl, enterTransition, exitTransition, configureSharedElementsReordered, fragment, z);
                    if (mergeTransitions != null) {
                        replaceHide(chooseImpl, exitTransition, fragment2, configureEnteringExitingViews);
                        ArrayList prepareSetNameOverridesReordered = chooseImpl.prepareSetNameOverridesReordered(arrayList);
                        chooseImpl.scheduleRemoveTargets(mergeTransitions, enterTransition, configureEnteringExitingViews2, exitTransition, configureEnteringExitingViews, configureSharedElementsReordered, arrayList);
                        chooseImpl.beginDelayedTransition(view2, mergeTransitions);
                        chooseImpl.setNameOverridesReordered(view2, arrayList2, arrayList, prepareSetNameOverridesReordered, arrayMap);
                        setViewVisibility(configureEnteringExitingViews2, 0);
                        chooseImpl.swapSharedElementTargets(configureSharedElementsReordered, arrayList2, arrayList);
                    }
                }
            }
        }
    }

    private static FragmentContainerTransition ensureContainer(FragmentContainerTransition fragmentContainerTransition, SparseArray<FragmentContainerTransition> sparseArray, int i) {
        if (fragmentContainerTransition != null) {
            return fragmentContainerTransition;
        }
        fragmentContainerTransition = new FragmentContainerTransition();
        sparseArray.put(i, fragmentContainerTransition);
        return fragmentContainerTransition;
    }

    private static String findKeyForValue(ArrayMap<String, String> arrayMap, String str) {
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            if (str.equals(arrayMap.valueAt(i))) {
                return (String) arrayMap.keyAt(i);
            }
        }
        return null;
    }

    private static Object getEnterTransition(FragmentTransitionImpl fragmentTransitionImpl, Fragment fragment, boolean z) {
        if (fragment == null) {
            return null;
        }
        return fragmentTransitionImpl.cloneTransition(z ? fragment.getReenterTransition() : fragment.getEnterTransition());
    }

    private static Object getExitTransition(FragmentTransitionImpl fragmentTransitionImpl, Fragment fragment, boolean z) {
        if (fragment == null) {
            return null;
        }
        return fragmentTransitionImpl.cloneTransition(z ? fragment.getReturnTransition() : fragment.getExitTransition());
    }

    static View getInEpicenterView(ArrayMap<String, View> arrayMap, FragmentContainerTransition fragmentContainerTransition, Object obj, boolean z) {
        BackStackRecord backStackRecord = fragmentContainerTransition.lastInTransaction;
        if (obj == null || arrayMap == null || backStackRecord.mSharedElementSourceNames == null || backStackRecord.mSharedElementSourceNames.isEmpty()) {
            return null;
        }
        Object obj2;
        if (z) {
            obj2 = (String) backStackRecord.mSharedElementSourceNames.get(0);
        } else {
            String obj22 = (String) backStackRecord.mSharedElementTargetNames.get(0);
        }
        return (View) arrayMap.get(obj22);
    }

    private static Object getSharedElementTransition(FragmentTransitionImpl fragmentTransitionImpl, Fragment fragment, Fragment fragment2, boolean z) {
        if (fragment == null || fragment2 == null) {
            return null;
        }
        return fragmentTransitionImpl.wrapTransitionInSet(fragmentTransitionImpl.cloneTransition(z ? fragment2.getSharedElementReturnTransition() : fragment.getSharedElementEnterTransition()));
    }

    private static Object mergeTransitions(FragmentTransitionImpl fragmentTransitionImpl, Object obj, Object obj2, Object obj3, Fragment fragment, boolean z) {
        boolean allowReturnTransitionOverlap = (obj == null || obj2 == null || fragment == null) ? true : z ? fragment.getAllowReturnTransitionOverlap() : fragment.getAllowEnterTransitionOverlap();
        return allowReturnTransitionOverlap ? fragmentTransitionImpl.mergeTransitionsTogether(obj2, obj, obj3) : fragmentTransitionImpl.mergeTransitionsInSequence(obj2, obj, obj3);
    }

    private static void replaceHide(FragmentTransitionImpl fragmentTransitionImpl, Object obj, Fragment fragment, final ArrayList<View> arrayList) {
        if (fragment != null && obj != null && fragment.mAdded && fragment.mHidden && fragment.mHiddenChanged) {
            fragment.setHideReplaced(true);
            fragmentTransitionImpl.scheduleHideFragmentView(obj, fragment.getView(), arrayList);
            OneShotPreDrawListener.add(fragment.mContainer, new Runnable() {
                public void run() {
                    FragmentTransition.setViewVisibility(arrayList, 4);
                }
            });
        }
    }

    private static FragmentTransitionImpl resolveSupportImpl() {
        try {
            return (FragmentTransitionImpl) Class.forName("android.support.transition.FragmentTransitionSupport").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            return null;
        }
    }

    private static void retainValues(ArrayMap<String, String> arrayMap, ArrayMap<String, View> arrayMap2) {
        for (int size = arrayMap.size() - 1; size >= 0; size--) {
            if (!arrayMap2.containsKey((String) arrayMap.valueAt(size))) {
                arrayMap.removeAt(size);
            }
        }
    }

    private static void scheduleTargetChange(FragmentTransitionImpl fragmentTransitionImpl, ViewGroup viewGroup, Fragment fragment, View view, ArrayList<View> arrayList, Object obj, ArrayList<View> arrayList2, Object obj2, ArrayList<View> arrayList3) {
        final Object obj3 = obj;
        final FragmentTransitionImpl fragmentTransitionImpl2 = fragmentTransitionImpl;
        final View view2 = view;
        final Fragment fragment2 = fragment;
        final ArrayList<View> arrayList4 = arrayList;
        final ArrayList<View> arrayList5 = arrayList2;
        final ArrayList<View> arrayList6 = arrayList3;
        final Object obj4 = obj2;
        OneShotPreDrawListener.add(viewGroup, new Runnable() {
            public void run() {
                if (obj3 != null) {
                    fragmentTransitionImpl2.removeTarget(obj3, view2);
                    arrayList5.addAll(FragmentTransition.configureEnteringExitingViews(fragmentTransitionImpl2, obj3, fragment2, arrayList4, view2));
                }
                if (arrayList6 != null) {
                    if (obj4 != null) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(view2);
                        fragmentTransitionImpl2.replaceTargets(obj4, arrayList6, arrayList);
                    }
                    arrayList6.clear();
                    arrayList6.add(view2);
                }
            }
        });
    }

    private static void setOutEpicenter(FragmentTransitionImpl fragmentTransitionImpl, Object obj, Object obj2, ArrayMap<String, View> arrayMap, boolean z, BackStackRecord backStackRecord) {
        if (backStackRecord.mSharedElementSourceNames != null && !backStackRecord.mSharedElementSourceNames.isEmpty()) {
            Object obj3;
            if (z) {
                obj3 = (String) backStackRecord.mSharedElementTargetNames.get(0);
            } else {
                String obj32 = (String) backStackRecord.mSharedElementSourceNames.get(0);
            }
            View view = (View) arrayMap.get(obj32);
            fragmentTransitionImpl.setEpicenter(obj, view);
            if (obj2 != null) {
                fragmentTransitionImpl.setEpicenter(obj2, view);
            }
        }
    }

    static void setViewVisibility(ArrayList<View> arrayList, int i) {
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ((View) arrayList.get(size)).setVisibility(i);
            }
        }
    }

    static void startTransitions(FragmentManagerImpl fragmentManagerImpl, ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2, int i, int i2, boolean z) {
        if (fragmentManagerImpl.mCurState >= 1) {
            SparseArray sparseArray = new SparseArray();
            for (int i3 = i; i3 < i2; i3++) {
                BackStackRecord backStackRecord = (BackStackRecord) arrayList.get(i3);
                if (((Boolean) arrayList2.get(i3)).booleanValue()) {
                    calculatePopFragments(backStackRecord, sparseArray, z);
                } else {
                    calculateFragments(backStackRecord, sparseArray, z);
                }
            }
            if (sparseArray.size() != 0) {
                View view = new View(fragmentManagerImpl.mHost.getContext());
                int size = sparseArray.size();
                for (int i4 = 0; i4 < size; i4++) {
                    int keyAt = sparseArray.keyAt(i4);
                    ArrayMap calculateNameOverrides = calculateNameOverrides(keyAt, arrayList, arrayList2, i, i2);
                    FragmentContainerTransition fragmentContainerTransition = (FragmentContainerTransition) sparseArray.valueAt(i4);
                    if (z) {
                        configureTransitionsReordered(fragmentManagerImpl, keyAt, fragmentContainerTransition, view, calculateNameOverrides);
                    } else {
                        configureTransitionsOrdered(fragmentManagerImpl, keyAt, fragmentContainerTransition, view, calculateNameOverrides);
                    }
                }
            }
        }
    }

    static boolean supportsTransition() {
        return (PLATFORM_IMPL == null && SUPPORT_IMPL == null) ? false : true;
    }
}
