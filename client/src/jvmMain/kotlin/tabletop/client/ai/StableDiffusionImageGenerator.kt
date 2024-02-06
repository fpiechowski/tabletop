package tabletop.client.ai

import io.github.robothy.sdwebui.sdk.SdWebui
import io.github.robothy.sdwebui.sdk.models.options.Txt2ImageOptions
import io.github.robothy.sdwebui.sdk.models.results.Txt2ImgResult
import tabletop.shared.dnd5e.character.Character
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


class StableDiffusionImageGenerator(
    endpointUrl: String
) : ImageGenerator {

    val stableDiffusion = SdWebui.create(endpointUrl)

    override fun Character.generateImage() {
        val txt2ImgResult: Txt2ImgResult = stableDiffusion.txt2Img(
            Txt2ImageOptions.builder()
                .prompt(toPrompt())
                .samplerName("DPM++ 2M Karras")
                .steps(20)
                .cfgScale(7.0)
                .seed(32749528)
                .build()
        )

        val step1Path: Path = Paths.get("docs/images/txt2img-dog.png")
        Files.write(step1Path, Base64.getDecoder().decode(txt2ImgResult.images.first()))
    }

    fun Character.toPrompt(): String = """"""
}