package tabletop.common.dnd5e.item

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.dnd5e.character.Proficiency

class Armor(
    override val name: String,
    val acBonus: Int,
    override val image: String? = null,
    override val id: UUID = UUID.generateUUID()
) : Item(), Equippable {

    enum class Type : Proficiency.Subject {
        Light, Medium, Heavy, Shield
    }
}
