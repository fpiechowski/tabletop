package tabletop.client.ai

import tabletop.shared.dnd5e.character.Character

interface ImageGenerator {

    fun Character.generateImage()
}