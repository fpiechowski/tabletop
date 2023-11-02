package tabletop.client.settings

import korlibs.math.geom.Size

data class Settings(
    var windowSize: Size = Size(1280, 720),
    var virtualSize: Size = Size(1280, 720)
)