package tabletop.client.dnd5e.character

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import arrow.core.raise.either
import arrow.core.raise.recover
import arrow.optics.copy
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.client.di.Dependencies
import tabletop.client.io.loadImageFile
import tabletop.client.state.add
import tabletop.client.ui.*
import tabletop.shared.dnd5e.character.*
import tabletop.shared.dnd5e.skill.Skill
import tabletop.shared.error.CommonError
import tabletop.shared.error.NotImplementedError
import tabletop.shared.error.UnsupportedSubtypeError
import tabletop.shared.event.CharacterUpdateRequested
import kotlin.time.Duration.Companion.seconds


@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@ExperimentalLayoutApi
fun characterWindowModel(character: Character, dependencies: Dependencies) = WindowModel(
    character.name,
    Modifier.width(600.dp).height(950.dp),
    MutableStateFlow(IntOffset.Zero),
    UUID.generateUUID()
) { CharacterView(character, dependencies) }
    .let { it.id to it }

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun CharacterView(character: Character, dependencies: Dependencies) {
    dependencies.state.run {
        dependencies.uiErrorHandler.run {

            val errors = remember { mutableStateOf(mapOf<Field<*>, CommonError>()) }
            val editMode = remember { mutableStateOf(true) }
            val updatedCharacter = remember { mutableStateOf(character) }
            val scrollState = rememberScrollState()
            val coroutineScope = rememberCoroutineScope()
            val autosaveJob = remember { mutableStateOf<Job?>(null) }

            fun resetAutosaveTimer() {
                autosaveJob.value?.cancel() // Cancel existing job
                autosaveJob.value = coroutineScope.launch {
                    delay(3.seconds)
                    dependencies.eventHandler.run {
                        recover(
                            {
                                CharacterUpdateRequested(
                                    dependencies.state.game.bind().id,
                                    updatedCharacter.value
                                ).handle().bind()
                            }
                        ) {
                            it.handle()
                        }
                    }
                }
            }

            @Composable
            fun characterAndPlayerNameRow() {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextField(
                        errors = errors,
                        editable = editMode,
                        modifier = Modifier.weight(1f),
                        field = CharacterViewFields.Name,
                        value = updatedCharacter.value.name,
                        textStyle = MaterialTheme.typography.headlineMedium,
                        singleLine = true,
                        onValueChange = {
                            updatedCharacter.value = character.apply {
                                name = it
                            }
                            resetAutosaveTimer()
                        }
                    )

                    if (updatedCharacter.value is PlayerCharacter) {
                        Button(
                            onClick = {
                                TODO("Implement Player View and open Player Window here")
                            },
                            modifier = Modifier,
                        ) {
                            Text((updatedCharacter.value as PlayerCharacter).player.name)
                        }
                    }
                }
            }

            @Composable
            fun sizeRaceClassesRow() {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val sizeMenuExpanded = remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        sizeMenuExpanded.value,
                        onExpandedChange = { sizeMenuExpanded.value = it }
                    ) {
                        updatedCharacter.value.race.sizes.forEach {
                            Text(it.name)
                        }
                    }

                    Text(
                        updatedCharacter.value.race.name
                    )

                    updatedCharacter.value.characterClassesLevels.forEach {
                        Button(onClick = {}) {
                            Text("${it.characterClass.name} (${it.level})")
                        }
                    }
                }
            }

            @Composable
            fun hpBox() {
                Box(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center).offset(y = 5.dp).height(20.dp),
                        progress = updatedCharacter.value.currentHp.toFloat() / updatedCharacter.value.hp.toFloat(),
                    )

                    Row(modifier = Modifier.align(Alignment.Center).padding(2.dp)) {
                        TextField(
                            errors = errors,
                            editable = editMode,
                            modifier = Modifier.width(100.dp),
                            field = CharacterViewFields.CurrentHP,
                            value = updatedCharacter.value.currentHp.toString(),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Right),
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
                                updatedCharacter.value = character.apply {
                                    currentHp = it
                                }

                                resetAutosaveTimer()
                            }
                        )

                        TextField(
                            errors = errors,
                            editable = editMode,
                            modifier = Modifier.width(100.dp),
                            field = CharacterViewFields.HP,
                            value = updatedCharacter.value.hp.toString(),
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
                                updatedCharacter.value = character.apply {
                                    hp = it
                                }

                                resetAutosaveTimer()
                            }
                        )
                    }
                }
            }

            @Composable
            fun experienceBox() {
                Box(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                        progress = updatedCharacter.value.experience.toFloat() / nextLevelExperience(updatedCharacter.value.experience),
                    )

                    Row(modifier = Modifier.align(Alignment.Center).padding(5.dp)) {
                        TextField(
                            errors = errors,
                            editable = editMode,
                            field = CharacterViewFields.Experience,
                            value = updatedCharacter.value.experience.toString(),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Right),
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
                                updatedCharacter.value = character.apply {
                                    experience = it
                                }

                                resetAutosaveTimer()
                            }
                        )

                        TextField(
                            errors = errors,
                            editable = editMode,
                            readOnly = true,
                            field = CharacterViewFields.NextLevelExperience,
                            value = nextLevelExperience(updatedCharacter.value.experience).toString(),
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
            }

            @Composable
            fun attributesRow() {
                updatedCharacter.value.attributes.forEach { attribute ->
                    Row {
                        Box(modifier = Modifier.width(100.dp), contentAlignment = Alignment.Center) {
                            recover({
                                OutlinedTextField(
                                    field = CharacterViewFields.forAttribute(attribute.key).bind(),
                                    value = attribute.value.toString(),
                                    modifier = Modifier.align(Alignment.Center),
                                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                                    errorHandler = { dependencies.state.errors.add(it) },
                                    onValueChange = { value: Int ->
                                        updatedCharacter.value = character.apply {
                                            attributes = attributes.copy {
                                                Character.Attributes.attributeLens(attribute.key) set value
                                            }
                                        }

                                        resetAutosaveTimer()
                                    }
                                )
                            }) {
                                coroutineScope.launch { it.handle() }
                            }

                            Text(
                                Character.Attribute.modifier(attribute.value).toString(),
                                modifier = Modifier.align(Alignment.CenterEnd).offset(y = 4.dp).padding(5.dp)
                            )
                            IconButton(
                                onClick = { coroutineScope.launch { NotImplementedError("Attribute Roll Button onClick").handle() } },
                                modifier = Modifier.align(Alignment.CenterStart).offset(y = 4.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Roll")
                            }
                        }
                    }
                }
            }

            @Composable
            fun characterImage(updatedCharacter: MutableState<Character>, modifier: Modifier = Modifier) {
                recover({
                    AsyncImage(
                        modifier = modifier.width(350.dp).padding(5.dp),
                        load = {
                            loadImageFile(
                                connectionDependencies.ensureNotNull().bind().assets.assetFile(
                                    updatedCharacter.value.image ?: Character.defaultImage
                                ).bind()
                            )
                        },
                        painterFor = { remember { BitmapPainter(it) } },
                        contentDescription = "Character Image",
                    )
                }) {
                    Icon(Icons.Default.Error, it.message)
                }
            }

            @Composable
            fun equipmentGrid() {
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(
                        items = with(updatedCharacter.value.equipment) {
                            listOf(
                                armor,
                                helmet,
                                gloves,
                                boots,
                                ring1,
                                ring2,
                                mainHandWeapon,
                                secondaryHandWeapon
                            )
                        }
                    ) {
                        Card(
                            modifier = Modifier.height(80.dp).padding(2.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Icon(
                                Icons.Default.Pending,
                                contentDescription = "Empty",
                                modifier = Modifier.fillMaxSize().padding(2.dp)
                            )
                        }
                    }
                }
            }

            @ExperimentalLayoutApi
            @Composable
            fun skillsFeaturesSpellsItemsRow() {
                Row {
                    val selectedCategory = remember { mutableStateOf(CharacterViewCategory.Skills) }

                    @Composable
                    fun navigation() {
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
                    }

                    navigation()

                    @Composable
                    fun skillsFlowRow() {
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

                    @Composable
                    fun featuresFlowRow() {
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

                    @Composable
                    fun spellsFlowRow() {
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

                    @Composable
                    fun itemsFlowRow() {
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

                    Column(modifier = Modifier.verticalScroll(scrollState)) {
                        when (selectedCategory.value) {
                            CharacterViewCategory.Skills -> {
                                skillsFlowRow()
                            }

                            CharacterViewCategory.Features -> {
                                featuresFlowRow()
                            }

                            CharacterViewCategory.Spells -> {
                                spellsFlowRow()
                            }

                            CharacterViewCategory.Items -> {
                                itemsFlowRow()
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp).fillMaxSize()) {
                characterAndPlayerNameRow()

                sizeRaceClassesRow()

                hpBox()

                experienceBox()

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column { attributesRow() }
                    Column { characterImage(updatedCharacter, Modifier.align(Alignment.CenterHorizontally)) }
                    Column { equipmentGrid() }
                }

                skillsFeaturesSpellsItemsRow()
            }
        }
    }
}

private object CharacterViewFields {
    fun forAttribute(key: Character.Attribute) = either {
        when (key) {
            Character.Attribute.Strength -> Strength
            Character.Attribute.Dexterity -> Dexterity
            Character.Attribute.Constitution -> Constitution
            Character.Attribute.Intelligence -> Intelligence
            Character.Attribute.Wisdom -> Wisdom
            Character.Attribute.Charisma -> Charisma
            else -> raise(UnsupportedSubtypeError(Character.Attribute::class))
        }
    }

    val NextLevelExperience = Field("Next Level Experience", Long::class)
    val CurrentHP = Field("Current HP", Int::class)
    val Experience = Field("Experience", Long::class)
    val Name = Field("Name", String::class)
    val HP = Field("HP", Int::class)
    val Strength = Field("STR", Int::class)
    val Dexterity = Field("DEX", Int::class)
    val Constitution = Field("CON", Int::class)
    val Intelligence = Field("Int", Int::class)
    val Wisdom = Field("WIS", Int::class)
    val Charisma = Field("CHA", Int::class)
}

private enum class CharacterViewCategory {
    Skills,
    Features,
    Spells,
    Items
}
