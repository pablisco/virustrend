package org.virustrend.design

import org.virustrend.design.text.Text

actual fun RenderScope.appLayout(
    properties: RenderProperties,
    block: AppLayoutScope.() -> Unit
) {

}

actual fun ParentRenderScope.mapGraph(
    properties: RenderProperties,
    dataPoints: List<MapGraphPoint>
) {

}

actual fun ParentRenderScope.infoBox(hint: Text, message: Text) {

}

