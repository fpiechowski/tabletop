package tabletop.shared.dnd5e

import arrow.optics.Lens
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import tabletop.shared.dnd5e.character.Character
import tabletop.shared.entity.Entity
import tabletop.shared.system.System

typealias Characters = Map<UUID, Character>

@Serializable
data class DnD5e(
    override val id: UUID = UUID(defaultIdValue),
    override val name: String = "Dungeons & Dragons - 5th Edition",
    override val image: String? = null,
    val characters: Characters = mapOf(),
) : System() {

    companion object {
        private const val defaultIdValue = "70452e48-ae88-43e3-b3f2-ea17d20b5bc3"

        val characters: Lens<DnD5e, Characters> = Lens(
            get = { it.characters },
            set = { dnd5e, characters -> dnd5e.copy(characters = characters) }
        )
    }

    override val entities: Map<UUID, Entity>
        get() = characters
}
