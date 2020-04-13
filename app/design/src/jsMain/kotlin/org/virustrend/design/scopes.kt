package org.virustrend.design

actual sealed class RenderScope
actual sealed class ParentRenderScope : RenderScope()
actual class AppLayoutScope : ParentRenderScope()