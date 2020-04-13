package org.virustrend.design

import org.virustrend.design.text.Text

expect fun RenderScope.appLayout(
    properties: RenderProperties,
    block: AppLayoutScope.() -> Unit
)

expect fun ParentRenderScope.mapGraph(
    properties: RenderProperties,
    dataPoints: List<MapGraphPoint>
)

expect fun ParentRenderScope.infoBox(hint: Text, message: Text)
