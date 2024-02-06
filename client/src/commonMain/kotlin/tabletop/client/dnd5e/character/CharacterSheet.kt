package tabletop.client.dnd5e.character

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.client.di.Dependencies
import tabletop.client.io.loadImageFile
import tabletop.client.ui.*
import tabletop.shared.dnd5e.character.Character
import tabletop.shared.dnd5e.character.nextLevelExperience
import tabletop.shared.dnd5e.skill.Skill
import tabletop.shared.error.CommonError
import tabletop.shared.error.NotImplementedError
import tabletop.shared.error.UnsupportedSubtypeError
import tabletop.shared.event.CharacterUpdateRequested
import kotlin.time.Duration.Companion.seconds


@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
class CharacterSheet(
    val dependencies: Dependencies,
    character: Character
) {
    private val character = MutableValue(character)
    private val errors = MutableValue(mapOf<Field<*>, CommonError>())
    private val editMode = MutableValue(true)
    private val ScrollState = ScrollState(0)
    private val autosaveJob = MutableValue<Job>(Job())

    @ExperimentalMaterial3Api
    @ExperimentalComposeUiApi
    @ExperimentalLayoutApi
    fun window() = Window(
        dependencies.childDependencies("characterSheet-${character.value.id}"),
        character.value.name,
        Modifier.width(600.dp).height(950.dp),
        MutableValue(IntOffset.Zero),
        UUID.generateUUID()
    ) { content() }

    @ExperimentalComposeUiApi
    @ExperimentalMaterial3Api
    @ExperimentalLayoutApi
    @Composable
    fun content() {
        val coroutineScope = rememberCoroutineScope()

        Column(modifier = Modifier.padding(10.dp).fillMaxSize()) {
            characterAndPlayerNameRow(character, errors, editMode, autosaveJob, coroutineScope)

            sizeRaceClassesRow(character)

            hpBox(character, errors, editMode, autosaveJob, coroutineScope)

            experienceBox(character, errors, editMode, autosaveJob, coroutineScope)

            Row(modifier = Modifier.fillMaxWidth()) {
                Column { attributesRow(character, autosaveJob, coroutineScope) }
                Column { characterImage(character, Modifier.align(Alignment.CenterHorizontally)) }
                Column { equipmentGrid(character) }
            }

            skillsFeaturesSpellsItemsRow(character, ScrollState)
        }
    }

    private fun resetAutosaveTimer(
        updatedCharacter: Value<Character>,
        autosaveJob: MutableValue<Job>,
        coroutineScope: CoroutineScope
    ) {

        autosaveJob.value.cancel() // Cancel existing job
        autosaveJob.value = coroutineScope.launch {
            delay(3.seconds)
            with(dependencies.eventHandler) {
                recover(
                    {
                        CharacterUpdateRequested(
                            dependencies.state.game.bind().id,
                            updatedCharacter.value
                        ).handle().bind()
                    }
                ) {
                    with(dependencies.errorDialogs) { it.handle() }
                }
            }
        }
    }

    @Composable
    fun characterAndPlayerNameRow(
        updatedCharacter: MutableValue<Character>,
        errors: MutableValue<Map<Field<*>, CommonError>>,
        editMode: Value<Boolean>,
        autosaveJob: MutableValue<Job>,
        coroutineScope: CoroutineScope
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextField(
                errors = errors,
                editable = editMode,
                modifier = Modifier.weight(1f),
                field = Fields.Name,
                value = updatedCharacter.value.name,
                textStyle = MaterialTheme.typography.headlineMedium,
                singleLine = true,
                onValueChange = { value: String ->
                    updatedCharacter.value = updatedCharacter.value.copy(name = value)
                    resetAutosaveTimer(updatedCharacter, autosaveJob, coroutineScope)
                }
            )

            updatedCharacter.value.player?.let {
                Button(
                    onClick = { TODO("Implement Player View and open Player Window here") },
                    modifier = Modifier,
                ) {
                    Text(it.name)
                }
            }
        }
    }

    @Composable
    fun sizeRaceClassesRow(
        updatedCharacter: Value<Character>
    ) {
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
    fun hpBox(
        updatedCharacter: MutableValue<Character>,
        errors: MutableValue<Map<Field<*>, CommonError>>,
        editMode: Value<Boolean>,
        autosaveJob: MutableValue<Job>,
        coroutineScope: CoroutineScope
    ) {
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
                    field = Fields.CurrentHP,
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
                        updatedCharacter.value = updatedCharacter.value.copy(currentHp = it)

                        resetAutosaveTimer(updatedCharacter, autosaveJob, coroutineScope)
                    }
                )

                TextField(
                    errors = errors,
                    editable = editMode,
                    modifier = Modifier.width(100.dp),
                    field = Fields.HP,
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
                        updatedCharacter.value = updatedCharacter.value.copy(hp = it)

                        resetAutosaveTimer(updatedCharacter, autosaveJob, coroutineScope)
                    }
                )
            }
        }
    }

    @Composable
    fun experienceBox(
        updatedCharacter: MutableValue<Character>,
        errors: MutableValue<Map<Field<*>, CommonError>>,
        editMode: Value<Boolean>,
        autosaveJob: MutableValue<Job>,
        coroutineScope: CoroutineScope
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                progress = updatedCharacter.value.experience.toFloat() / nextLevelExperience(
                    updatedCharacter.value.experience
                ),
            )

            Row(modifier = Modifier.align(Alignment.Center).padding(5.dp)) {
                TextField(
                    errors = errors,
                    editable = editMode,
                    field = Fields.Experience,
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
                        updatedCharacter.value = updatedCharacter.value.copy(experience = it)

                        resetAutosaveTimer(updatedCharacter, autosaveJob, coroutineScope)
                    }
                )

                TextField(
                    errors = errors,
                    editable = editMode,
                    readOnly = true,
                    field = Fields.NextLevelExperience,
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
    fun attributesRow(
        updatedCharacter: MutableValue<Character>,
        autosaveJob: MutableValue<Job>,
        coroutineScope: CoroutineScope
    ) {
        updatedCharacter.value.attributes.forEach { attribute ->
            Row {
                Box(modifier = Modifier.width(100.dp), contentAlignment = Alignment.Center) {
                    recover({
                        OutlinedTextField(
                            field = Fields.forAttribute(attribute.key).bind(),
                            value = attribute.value.toString(),
                            modifier = Modifier.align(Alignment.Center),
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                            errorHandler = { error -> dependencies.errorDialogs.errors.update { it + error } },
                            onValueChange = { value: Int ->
                                updatedCharacter.value = updatedCharacter.value.copy {
                                    Character.attributes compose Character.Attributes.lensFor(attribute.key) set value
                                }

                                resetAutosaveTimer(updatedCharacter, autosaveJob, coroutineScope)
                            }
                        )
                    }) {
                        coroutineScope.launch { with(dependencies.errorDialogs) { it.handle() } }
                    }

                    Text(
                        Character.Attribute.modifier(attribute.value).toString(),
                        modifier = Modifier.align(Alignment.CenterEnd).offset(y = 4.dp).padding(5.dp)
                    )
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                with(dependencies.errorDialogs) {
                                    NotImplementedError("Attribute Roll Button onClick").handle()
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterStart).offset(y = 4.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Roll")
                    }
                }
            }
        }
    }

    @Composable
    fun characterImage(updatedCharacter: MutableValue<Character>, modifier: Modifier = Modifier) {
        recover({
            AsyncImage(
                modifier = modifier.width(350.dp).padding(5.dp),
                load = {
                    loadImageFile(
                        with(dependencies.state) {
                            connectionDependencies.ensureNotNull().bind()
                                .assets.assetFile(
                                    updatedCharacter.value.image ?: Character.defaultImage
                                ).bind()
                        }
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
    fun equipmentGrid(
        updatedCharacter: MutableValue<Character>
    ) {
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
    fun skillsFeaturesSpellsItemsRow(updatedCharacter: MutableValue<Character>, ScrollState: ScrollState) {
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

            Column(modifier = Modifier.verticalScroll(ScrollState)) {
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

    private object Fields {
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
}
