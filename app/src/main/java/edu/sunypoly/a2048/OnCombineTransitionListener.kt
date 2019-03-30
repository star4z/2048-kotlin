package edu.sunypoly.a2048

import android.support.constraint.ConstraintLayout
import android.transition.Transition
import android.util.Log
import android.widget.TextView

@Suppress("KDocUnresolvedReference")
class OnCombineTransitionListener(
        private val mainActivity: MainActivity,
        private val parent: ConstraintLayout,
        private val pos: Pos,
        private val value: Int,
        private vararg val tiles: TextView
) : Transition.TransitionListener {

    /**
     * Notification about the end of the transition. Canceled transitions
     * will always notify listeners of both the cancellation and end
     * events. That is, [.onTransitionEnd] is always called,
     * regardless of whether the transition was canceled or played
     * through to completion.
     *
     * @param transition The transition which reached its end.
     */
    override fun onTransitionEnd(transition: Transition?) {
        tiles.forEach {
            Log.d(TAG(this), "Removing ")
            parent.removeView(it)
        }
        mainActivity.addAt(pos, value)
    }

    /**
     * Notification when a transition is resumed.
     * Note that resume() may be called by a parent [TransitionSet] on
     * a child transition which has not yet started. This allows the child
     * transition to restore state which may have changed in an earlier call
     * to [.onTransitionPause].
     *
     * @param transition The transition which was resumed.
     */
    override fun onTransitionResume(transition: Transition?) {

    }

    /**
     * Notification when a transition is paused.
     * Note that createAnimator() may be called by a parent [TransitionSet] on
     * a child transition which has not yet started. This allows the child
     * transition to restore state on target objects which was set at
     * [ createAnimator()][.createAnimator] time.
     *
     * @param transition The transition which was paused.
     */
    override fun onTransitionPause(transition: Transition?) {

    }

    /**
     * Notification about the cancellation of the transition.
     * Note that cancel may be called by a parent [TransitionSet] on
     * a child transition which has not yet started. This allows the child
     * transition to restore state on target objects which was set at
     * [ createAnimator()][.createAnimator] time.
     *
     * @param transition The transition which was canceled.
     */
    override fun onTransitionCancel(transition: Transition?) {

    }

    /**
     * Notification about the start of the transition.
     *
     * @param transition The started transition.
     */
    override fun onTransitionStart(transition: Transition?) {

    }
}