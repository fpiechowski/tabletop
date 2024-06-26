package tabletop.shared.dnd5e.item

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.shared.dnd5e.IntModifier
import tabletop.shared.dnd5e.Modifier
import tabletop.shared.dnd5e.character.Proficiency

data class Armor(
    override val name: String,
    val acModifier: IntModifier,
    override val image: String? = null,
    override val id: UUID = UUID.generateUUID()
) : Item(), Equippable {

    enum class Type : Proficiency.Subject {
        Light, Medium, Heavy, Shield
    }
}
