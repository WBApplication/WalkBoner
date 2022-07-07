package com.fusoft.walkboner.utils;

import android.animation.LayoutTransition;
import android.widget.LinearLayout;

import de.dlyt.yanndroid.oneui.widget.RoundLinearLayout;

public class AnimateChanges {
    public static void forLinear(LinearLayout linear) {
        linear.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    public static void forLinear(RoundLinearLayout linear) {
        linear.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }
}
