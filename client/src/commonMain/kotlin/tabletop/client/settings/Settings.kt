package tabletop.client.settings

import korlibs.math.geom.Size

data class Settings(
    var windowSize: Size = Size(1920, 1080),
    var virtualSize: Size = Size(1920, 1080)
)