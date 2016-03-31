/*
 * BottomNavigationLayout library for Android
 * Copyright (c) 2016. Nikola Despotoski (http://github.com/NikolaDespotoski).
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package despotoski.nikola.github.com.bottomnavigationlayout;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * Created by Nikola D. on 3/31/2016.
 */
public class BottomNavigationFloatingActionButtonBehavior extends FloatingActionButton.Behavior {

    private ViewPropertyAnimatorCompat mFabTranslator;
    private int mFabBottomMargin;
    private int mTargetFabTranslationY;
    private int mInitialTranslationY;
    private boolean isSnackBarShown = false;

    public BottomNavigationFloatingActionButtonBehavior() {
    }

    public BottomNavigationFloatingActionButtonBehavior(Context context, AttributeSet attributeSet) {

    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency) || dependency instanceof BottomTabLayout;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        boolean superDep = super.onDependentViewChanged(parent, child, dependency);
        if (dependency instanceof BottomTabLayout) {
            updateFabForBottomBar(parent, dependency, child);
            return true;
        }
        return superDep;
    }

    private float getSnackBarHeight(View dependency) {
        return dependency.getHeight() - dependency.getPaddingBottom();
    }

    private void updateFabForBottomBar(CoordinatorLayout parent, View dependency, FloatingActionButton child) {
        findTargetTranslation(dependency);
        ViewCompat.setTranslationY(child, mTargetFabTranslationY);

    }

    private void findTargetTranslation(View dependency) {
        int dependencyTranslationY = (int) ViewCompat.getTranslationY(dependency);
        mTargetFabTranslationY = (dependencyTranslationY - dependency.getHeight());
    }

    private float getFabTranslationYFromDependencies(CoordinatorLayout parent,
                                                     FloatingActionButton fab) {
        float minOffset = 0;
        final List<View> dependencies = parent.getDependencies(fab);
        for (int i = 0, z = dependencies.size(); i < z; i++) {
            final View view = dependencies.get(i);
            if (view instanceof BottomTabLayout && parent.doViewsOverlap(fab, view)) {
                minOffset = Math.min(minOffset,
                        ViewCompat.getTranslationY(view) - view.getHeight());
            }
        }

        return minOffset;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child, int layoutDirection) {
        boolean onLayoutChild = super.onLayoutChild(parent, child, layoutDirection);
        if (child.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            if (layoutParams.getAnchorId() != View.NO_ID) return onLayoutChild;
            mFabBottomMargin = layoutParams.bottomMargin;
        }
        List<View> dependencies = parent.getDependencies(child);
        for (int i = dependencies.size() - 1; i >= 0; i--) {
            View view = dependencies.get(i);
            if (view instanceof BottomTabLayout) {
                mInitialTranslationY = -view.getHeight();
                ViewCompat.setTranslationY(child, mInitialTranslationY);
            }
        }
        return true;
    }
}