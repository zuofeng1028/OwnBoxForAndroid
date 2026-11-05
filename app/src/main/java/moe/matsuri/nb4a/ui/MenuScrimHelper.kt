package moe.matsuri.nb4a.ui

import android.app.Activity
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import io.nekohasekai.sagernet.ui.MainActivity

object MenuScrimHelper {

    fun showPopupMenuWithScrim(
        popup: PopupMenu,
        activity: Activity?,
        onDismiss: (() -> Unit)? = null
    ) {
        val main = activity as? MainActivity
        if (main != null) {
            val token = popup
            main.showMenuScrim(token) {
                try {
                    popup.dismiss()
                } catch (_: Throwable) {}
            }
            popup.setOnDismissListener {
                onDismiss?.invoke()
                main.hideMenuScrim(token)
            }
        } else if (onDismiss != null) {
            popup.setOnDismissListener { onDismiss.invoke() }
        }
        popup.show()
    }

    fun hookToolbar(toolbar: Toolbar, activity: MainActivity) {
        toolbar.post {
            val amv = toolbar.children.filterIsInstance<ActionMenuView>().firstOrNull() ?: return@post
            hookActionMenuView(toolbar, amv, activity)
        }
    }

    fun hookActionMenuView(toolbar: Toolbar, amv: ActionMenuView, activity: MainActivity) {
        val touchListener = View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                scheduleToolbarCheck(toolbar, amv, activity)
            }
            false
        }

        amv.setOnTouchListener(touchListener)
        for (i in 0 until amv.childCount) {
            amv.getChildAt(i).setOnTouchListener(touchListener)
        }
    }

    fun scheduleToolbarCheck(toolbar: Toolbar, amv: ActionMenuView, activity: MainActivity) {
        toolbar.post {
            checkAndHookActiveToolbarPopups(toolbar, amv, activity)
        }
        toolbar.postDelayed({
            checkAndHookActiveToolbarPopups(toolbar, amv, activity)
        }, 60)
        toolbar.postDelayed({
            checkAndHookActiveToolbarPopups(toolbar, amv, activity)
        }, 150)
    }

    fun checkAndHookActiveToolbarPopups(toolbar: Toolbar, amv: ActionMenuView, activity: MainActivity) {
        try {
            val presenterField = ActionMenuView::class.java.getDeclaredField("mPresenter")
            presenterField.isAccessible = true
            val presenter = presenterField.get(amv) ?: return

            var showingHelper: MenuPopupHelper? = null

            // 1. Check mOverflowPopup
            try {
                val overflowField = presenter.javaClass.getDeclaredField("mOverflowPopup")
                overflowField.isAccessible = true
                val overflowPopup = overflowField.get(presenter) as? MenuPopupHelper
                if (overflowPopup != null && overflowPopup.isShowing) {
                    showingHelper = overflowPopup
                }
            } catch (_: Throwable) {}

            // 2. Check mActionButtonPopup (e.g. Add profile submenu)
            if (showingHelper == null) {
                try {
                    val actionButtonPopupField = presenter.javaClass.getDeclaredField("mActionButtonPopup")
                    actionButtonPopupField.isAccessible = true
                    val actionButtonPopup = actionButtonPopupField.get(presenter) as? MenuPopupHelper
                    if (actionButtonPopup != null && actionButtonPopup.isShowing) {
                        showingHelper = actionButtonPopup
                    }
                } catch (_: Throwable) {}
            }

            if (showingHelper != null || toolbar.isOverflowMenuShowing) {
                val token = "toolbar_popup_${System.identityHashCode(showingHelper ?: toolbar)}"
                activity.showMenuScrim(token) {
                    try {
                        toolbar.dismissPopupMenus()
                    } catch (_: Throwable) {}
                }
                showingHelper?.setOnDismissListener {
                    activity.hideMenuScrim(token)
                }
            }
        } catch (_: Throwable) {}
    }
}
