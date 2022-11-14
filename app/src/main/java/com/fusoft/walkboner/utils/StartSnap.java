package com.fusoft.walkboner.utils;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
        mv = {1, 6, 0},
        k = 1,
        d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0015\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\b\u0016\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001a\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\u0018\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u0004H\u0004J\u001a\u0010\u0015\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0014\u001a\u00020\u0004H\u0004J\u0012\u0010\u0016\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0010\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000fH\u0004J\u0010\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000fH\u0004R\u001c\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0084\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001c\u0010\t\u001a\u0004\u0018\u00010\u0004X\u0084\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u0006\"\u0004\b\u000b\u0010\b¨\u0006\u0019"},
        d2 = {"Lcom/fusoft/walkboner/utils/StartSnap;", "Lde/dlyt/yanndroid/oneui/sesl/recyclerview/LinearSnapHelper;", "()V", "_horizontalHelper", "Lde/dlyt/yanndroid/oneui/sesl/recyclerview/OrientationHelper;", "get_horizontalHelper", "()Lde/dlyt/yanndroid/oneui/sesl/recyclerview/OrientationHelper;", "set_horizontalHelper", "(Lde/dlyt/yanndroid/oneui/sesl/recyclerview/OrientationHelper;)V", "_verticalHelper", "get_verticalHelper", "set_verticalHelper", "calculateDistanceToFinalSnap", "", "layoutManager", "Lde/dlyt/yanndroid/oneui/view/RecyclerView$LayoutManager;", "targetView", "Landroid/view/View;", "distanceToStart", "", "helper", "findFirstView", "findSnapView", "getHorizontalHelper", "getVerticalHelper", "WalkBoner.app.main"}
)
public class StartSnap extends LinearSnapHelper {
    @Nullable
    private OrientationHelper _verticalHelper;
    @Nullable
    private OrientationHelper _horizontalHelper;

    @Nullable
    protected final OrientationHelper get_verticalHelper() {
        return this._verticalHelper;
    }

    protected final void set_verticalHelper(@Nullable OrientationHelper var1) {
        this._verticalHelper = var1;
    }

    @Nullable
    protected final OrientationHelper get_horizontalHelper() {
        return this._horizontalHelper;
    }

    protected final void set_horizontalHelper(@Nullable OrientationHelper var1) {
        this._horizontalHelper = var1;
    }

    @Nullable
    public View findSnapView(@NotNull RecyclerView.LayoutManager layoutManager) {
        Intrinsics.checkNotNullParameter(layoutManager, "layoutManager");
        if (layoutManager.canScrollVertically()) {
            return this.findFirstView(layoutManager, this.getVerticalHelper(layoutManager));
        } else {
            return layoutManager.canScrollHorizontally() ? this.findFirstView(layoutManager, this.getHorizontalHelper(layoutManager)) : null;
        }
    }

    @Nullable
    public int[] calculateDistanceToFinalSnap(@NotNull RecyclerView.LayoutManager layoutManager, @NotNull View targetView) {
        Intrinsics.checkNotNullParameter(layoutManager, "layoutManager");
        Intrinsics.checkNotNullParameter(targetView, "targetView");
        byte var4 = 2;
        int[] var5 = new int[var4];

        for(int var6 = 0; var6 < var4; ++var6) {
            boolean var8 = false;
            byte var11 = 0;
            var5[var6] = var11;
        }

        if (layoutManager.canScrollHorizontally()) {
            var5[0] = this.distanceToStart(targetView, this.getHorizontalHelper(layoutManager));
        } else {
            var5[0] = 0;
        }

        if (layoutManager.canScrollVertically()) {
            var5[1] = this.distanceToStart(targetView, this.getVerticalHelper(layoutManager));
        } else {
            var5[1] = 0;
        }

        return var5;
    }

    @Nullable
    protected final View findFirstView(@NotNull RecyclerView.LayoutManager layoutManager, @NotNull OrientationHelper helper) {
        Intrinsics.checkNotNullParameter(layoutManager, "layoutManager");
        Intrinsics.checkNotNullParameter(helper, "helper");
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        } else {
            if (layoutManager instanceof LinearLayoutManager) {
                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastCompletelyVisibleItemPosition();
                if (lastVisibleItemPosition == ((LinearLayoutManager)layoutManager).getItemCount() - 1) {
                    return null;
                }
            }

            View closestChild = (View)null;
            int start = 0;
            if (layoutManager.getClipToPadding()) {
                start = helper.getStartAfterPadding();
            }

            int absClosest = Integer.MAX_VALUE;
            int i = 0;
            int var8 = childCount - 1;
            if (i <= var8) {
                while(true) {
                    View child = layoutManager.getChildAt(i);
                    int childStart = helper.getDecoratedStart(child);
                    int absDistance = Math.abs(childStart - start);
                    if (absDistance < absClosest) {
                        absClosest = absDistance;
                        closestChild = child;
                    }

                    if (i == var8) {
                        break;
                    }

                    ++i;
                }
            }

            return closestChild;
        }
    }

    protected final int distanceToStart(@NotNull View targetView, @NotNull OrientationHelper helper) {
        Intrinsics.checkNotNullParameter(targetView, "targetView");
        Intrinsics.checkNotNullParameter(helper, "helper");
        return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
    }

    @NotNull
    protected final OrientationHelper getVerticalHelper(@NotNull RecyclerView.LayoutManager layoutManager) {
        Intrinsics.checkNotNullParameter(layoutManager, "layoutManager");
        if (this._verticalHelper == null) {
            this._verticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }

        OrientationHelper var10000 = this._verticalHelper;
        Intrinsics.checkNotNull(var10000);
        return var10000;
    }

    @NotNull
    protected final OrientationHelper getHorizontalHelper(@NotNull RecyclerView.LayoutManager layoutManager) {
        Intrinsics.checkNotNullParameter(layoutManager, "layoutManager");
        if (this._horizontalHelper == null) {
            this._horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }

        OrientationHelper var10000 = this._horizontalHelper;
        Intrinsics.checkNotNull(var10000);
        return var10000;
    }
}