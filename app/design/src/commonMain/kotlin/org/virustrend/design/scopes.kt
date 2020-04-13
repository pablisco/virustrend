package org.virustrend.design

expect sealed class RenderScope
expect sealed class ParentRenderScope : RenderScope
expect class AppLayoutScope : ParentRenderScope