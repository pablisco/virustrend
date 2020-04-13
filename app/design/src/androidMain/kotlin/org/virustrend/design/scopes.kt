package org.virustrend.design

import android.view.View
import android.view.ViewGroup

actual sealed class RenderScope(val view: View)
actual sealed class ParentRenderScope(val viewGroup: ViewGroup) : RenderScope(viewGroup)
actual class AppLayoutScope(root: ViewGroup) : ParentRenderScope(root)