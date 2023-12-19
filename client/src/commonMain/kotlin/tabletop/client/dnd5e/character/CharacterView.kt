package tabletop.client.dnd5e.character

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import arrow.optics.copy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.client.ui.WindowModel
import tabletop.common.dnd5e.character.*
import tabletop.common.dnd5e.skill.Skill
import tabletop.common.error.CommonError
import kotlin.reflect.KClass


data class CharacterViewField<T : Any>(
    val label: String,
    val type: KClass<T>
) {
    companion object {
        val NextLevelExperience = CharacterViewField("Next Level Experience", Long::class)
        val CurrentHP = CharacterViewField("Current HP", Int::class)
        val Experience = CharacterViewField("Experience", Long::class)
        val Name = CharacterViewField("Name", String::class)
        val HP = CharacterViewField("HP", Int::class)
    }
}

@ExperimentalLayoutApi
fun CharacterWindowModel(character: Character) = WindowModel(
    character.name,
    Modifier.width(600.dp).height(800.dp),
    MutableStateFlow(IntOffset.Zero),
    UUID.generateUUID()
) { CharacterView(character) }
    .let { it.id to it }

@ExperimentalLayoutApi
@Composable
private fun CharacterView(character: Character) {
    val errors = remember { mutableStateOf(mapOf<CharacterViewField<*>, CommonError>()) }
    val editMode = remember { mutableStateOf(true) }
    val updatedCharacter = remember { mutableStateOf(character) }

    @Composable
    fun <T : Any> TextField(
        modifier: Modifier = Modifier,
        field: CharacterViewField<T>,
        value: String,
        textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
        singleLine: Boolean = false,
        fromString: (String) -> T,
        colors: TextFieldColors = TextFieldDefaults.colors(),
        readOnly: Boolean? = null,
        onValueChange: (T) -> Unit,
    ) = TextField(
        readOnly = readOnly ?: !editMode.value,
        value = value,
        label = { Text(field.label) },
        isError = errors.value.containsKey(field),
        onValueChange = {
            onValueChange(fromString(it))
        },
        textStyle = textStyle,
        modifier = modifier,
        singleLine = singleLine,
        colors = colors
    )

    val scrollState = rememberScrollState()

    Card(modifier = Modifier.verticalScroll(scrollState).padding(10.dp)) {
        Column(modifier = Modifier.padding(2.dp).fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().border(1.dp, Color.Red),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextField(
                    modifier = Modifier.weight(1f).border(1.dp, Color.Blue),
                    field = CharacterViewField.Name,
                    value = updatedCharacter.value.name,
                    textStyle = MaterialTheme.typography.headlineMedium,
                    singleLine = true,
                    fromString = { it },
                    onValueChange = {
                        updatedCharacter.value = character.copy {
                            Character.name set it
                        }
                    }
                )

                if (updatedCharacter.value is PlayerCharacter) {
                    Button(
                        onClick = {},
                        modifier = Modifier.border(1.dp, Color.Blue),
                    ) {
                        Text((updatedCharacter.value as PlayerCharacter).player.name)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                Text(
                    updatedCharacter.value.race.size.name
                )

                Text(
                    updatedCharacter.value.race.name
                )

                Text(
                    updatedCharacter.value.characterClassesLevels.joinToString(", ") { "${it.characterClass.name} (${it.level})" },
                )
            }

            Box(modifier = Modifier.fillMaxWidth().border(1.dp, Color.Blue)) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center).offset(y = 8.dp).height(20.dp),
                    progress = updatedCharacter.value.currentHp.toFloat() / updatedCharacter.value.hp.toFloat(),
                )

                Row(modifier = Modifier.align(Alignment.Center).padding(5.dp)) {
                    TextField(
                        modifier = Modifier.width(100.dp),
                        field = CharacterViewField.CurrentHP,
                        value = updatedCharacter.value.currentHp.toString(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Right),
                        fromString = { it.toInt() },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                        ),
                        onValueChange = {
                            updatedCharacter.value = character.copy {
                                Character.currentHp set it
                            }
                        }
                    )

                    TextField(
                        modifier = Modifier.width(100.dp),
                        field = CharacterViewField.HP,
                        value = updatedCharacter.value.hp.toString(),
                        fromString = { it.toInt() },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                        ),
                        onValueChange = {
                            updatedCharacter.value = character.copy {
                                Character.hp set it
                            }
                        }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().border(1.dp, Color.Blue)) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                    progress = updatedCharacter.value.experience.toFloat() / nextLevelExperience(updatedCharacter.value.experience),
                )

                Row(modifier = Modifier.align(Alignment.Center).padding(5.dp)) {
                    TextField(
                        field = CharacterViewField.Experience,
                        value = updatedCharacter.value.experience.toString(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Right),
                        fromString = { it.toLong() },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                        ),
                        onValueChange = {
                            updatedCharacter.value = character.copy {
                                Character.experience set it
                            }
                        }
                    )

                    TextField(
                        readOnly = true,
                        field = CharacterViewField.NextLevelExperience,
                        value = nextLevelExperience(updatedCharacter.value.experience).toString(),
                        fromString = { it.toLong() },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                        ),
                        onValueChange = {}
                    )
                }
            }

            updatedCharacter.value.attributes.forEach { attribute ->
                Row {
                    Text(
                        "${attribute.key.name}: ${attribute.value} (${Character.Attribute.modifier(attribute.value).value})",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Refresh, contentDescription = "Ability Check")
                    }
                    IconButton(onClick = {}, modifier = Modifier.width(20.dp)) {
                        Icon(Icons.Default.HideImage, contentDescription = "Saving Throw")
                    }
                }
            }

            Row {
                val selectedCategory = remember { mutableStateOf(CharacterViewCategory.Skills) }

                NavigationRail(containerColor = Color.Transparent) {
                    NavigationRailItem(
                        selected = selectedCategory.value == CharacterViewCategory.Skills,
                        onClick = { selectedCategory.value = CharacterViewCategory.Skills },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Skills") },
                        label = { Text("Skills") }
                    )

                    NavigationRailItem(
                        selected = selectedCategory.value == CharacterViewCategory.Features,
                        onClick = { selectedCategory.value = CharacterViewCategory.Features },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Features") },
                        label = { Text("Features") }
                    )

                    NavigationRailItem(
                        selected = selectedCategory.value == CharacterViewCategory.Spells,
                        onClick = { selectedCategory.value = CharacterViewCategory.Spells },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Spells") },
                        label = { Text("Spells") }
                    )

                    NavigationRailItem(
                        selected = selectedCategory.value == CharacterViewCategory.Items,
                        onClick = { selectedCategory.value = CharacterViewCategory.Items },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Items") },
                        label = { Text("Items") }
                    )
                }

                when (selectedCategory.value) {
                    CharacterViewCategory.Skills -> {
                        FlowRow(
                            modifier = Modifier.padding(5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Skill.all.forEach {
                                Button({}) {
                                    Text(it.name)
                                }
                            }
                        }
                    }

                    CharacterViewCategory.Features -> {
                        FlowRow(
                            modifier = Modifier.padding(5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            updatedCharacter.value.features.forEach {
                                Button({}) {
                                    Text(it.name)
                                }
                            }
                        }
                    }

                    CharacterViewCategory.Spells -> {
                        FlowRow(
                            modifier = Modifier.padding(5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            updatedCharacter.value.spells.forEach {
                                Button({}) {
                                    Text(it.name)
                                }
                            }
                        }
                    }

                    CharacterViewCategory.Items -> {
                        FlowRow(
                            modifier = Modifier.padding(5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            updatedCharacter.value.items.forEach {
                                Button({}) {
                                    Text(it.name)
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}

enum class CharacterViewCategory {
    Skills,
    Features,
    Spells,
    Items
}
