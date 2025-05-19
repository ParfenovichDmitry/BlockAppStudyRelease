package pl.parfen.blockappstudyrelease.ui.ai.components

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import pl.parfen.blockappstudyrelease.data.model.ai.ChatGPTManager
import pl.parfen.blockappstudyrelease.ui.ai.components.elements.*
import pl.parfen.blockappstudyrelease.ui.theme.GreenLight
import pl.parfen.blockappstudyrelease.ui.theme.GreenMedium
import pl.parfen.blockappstudyrelease.util.HelpMethods.calculateOneMonthLater
import pl.parfen.blockappstudyrelease.util.HelpMethods.createPrompt
import pl.parfen.blockappstudyrelease.util.HelpMethods.mapTopicToEnglish

@Composable
fun AIStateHandler(
    profileId: Int,
    age: String,
    appLanguage: String,
    additionalLanguage: String?,
    aiNetwork: String,
    aiTopics: List<String>,
    aiLanguage: String,
    selectedTopics: List<String>,
    onTopicsUpdated: (List<Topic>, List<String>) -> Unit
) {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences("AI_$profileId", Context.MODE_PRIVATE)
    }

    val languageNames = context.resources.getStringArray(pl.parfen.blockappstudyrelease.R.array.available_languages).toList()
    val languageCodes = context.resources.getStringArray(pl.parfen.blockappstudyrelease.R.array.language_codes).toList()
    val localizedAppLanguage = languageNames.getOrElse(languageCodes.indexOf(appLanguage.lowercase())) { languageNames[0] }

    var selectedLanguageIndex by rememberSaveable { mutableStateOf(languageNames.indexOf(localizedAppLanguage).coerceAtLeast(0)) }
    var selectedAdditionalLanguageIndex by rememberSaveable { mutableStateOf(additionalLanguage?.let { languageNames.indexOf(it).takeIf { i -> i != -1 } } ?: -1) }
    var useOnlySecondLanguage by rememberSaveable { mutableStateOf(prefs.getBoolean("useOnlySecondLanguage", false)) }
    var breakIntoSyllables by rememberSaveable { mutableStateOf(prefs.getBoolean("breakIntoSyllables", false)) }
    var isSubscribed by remember { mutableStateOf(prefs.getBoolean("is_subscribed", false)) }
    var subscriptionEndDate by remember { mutableStateOf(prefs.getString("subscription_end_date", "") ?: "") }
    var freeAttempts by remember { mutableStateOf(prefs.getInt("free_attempts", 5)) }
    var selectedTopicIndex by rememberSaveable { mutableStateOf(prefs.getInt("selectedTopicIndex", 0)) }
    var currentTopicIndex by rememberSaveable { mutableStateOf(prefs.getInt("currentTopicIndex", 0)) }
    var aiResponse by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val chatGPTManager = remember { ChatGPTManager(Handler(Looper.getMainLooper())) }
    val lazyListState = rememberLazyListState()

    val userTopicsState = rememberSaveable(stateSaver = Saver(
        save = { list -> list.map { "${it.original}|${it.english}" } },
        restore = { saved -> saved.map { val parts = it.split("|"); Topic(parts[0], parts[1]) }.toMutableList() }
    )) {
        mutableStateOf(
            aiTopics.map { Topic(it, mapTopicToEnglish(context, it, languageCodes)) }.toMutableList()
        )
    }

    var selectedTopicsState by remember { mutableStateOf(selectedTopics) }

    LaunchedEffect(
        selectedLanguageIndex,
        selectedAdditionalLanguageIndex,
        useOnlySecondLanguage,
        breakIntoSyllables,
        isSubscribed,
        subscriptionEndDate,
        freeAttempts,
        selectedTopicIndex,
        currentTopicIndex
    ) {
        with(prefs.edit()) {
            putInt("selectedLanguageIndex", selectedLanguageIndex)
            putInt("selectedAdditionalLanguageIndex", selectedAdditionalLanguageIndex)
            putBoolean("useOnlySecondLanguage", useOnlySecondLanguage)
            putBoolean("breakIntoSyllables", breakIntoSyllables)
            putBoolean("is_subscribed", isSubscribed)
            putString("subscription_end_date", subscriptionEndDate)
            putInt("free_attempts", freeAttempts)
            putInt("selectedTopicIndex", selectedTopicIndex)
            putInt("currentTopicIndex", currentTopicIndex)
            apply()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = androidx.compose.ui.graphics.Brush.verticalGradient(listOf(GreenLight, GreenMedium)))
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            AIHeaderSection(
                age = age,
                languageNames = languageNames,
                selectedLanguageIndex = selectedLanguageIndex,
                selectedAdditionalLanguageIndex = selectedAdditionalLanguageIndex,
                onLanguageChange = { selectedLanguageIndex = it },
                onAdditionalLanguageChange = { selectedAdditionalLanguageIndex = it },
                useOnlySecondLanguage = useOnlySecondLanguage,
                onUseOnlySecondLanguageChange = { useOnlySecondLanguage = it },
                breakIntoSyllables = breakIntoSyllables,
                onBreakIntoSyllablesChange = { breakIntoSyllables = it },
                freeAttempts = freeAttempts,
                isSubscribed = isSubscribed,
                subscriptionEndDate = subscriptionEndDate,
                onSubscribe = {
                    isSubscribed = true
                    subscriptionEndDate = calculateOneMonthLater()
                },
                onResetAttempts = { freeAttempts = 5 }
            )

            AITopicSelector(
                userTopics = userTopicsState.value,
                selectedTopicsState = selectedTopicsState,
                onTopicsUpdated = { updated, selected ->
                    userTopicsState.value = updated.toMutableList()
                    selectedTopicsState = selected
                    onTopicsUpdated(updated, selected)
                },
                context = context,
                selectedTopicIndex = selectedTopicIndex,
                onTopicSelected = { selectedTopicIndex = it },
                generateOnlySelectedTopics = false,
                onGenerateOnlySelectedChange = {}
            )

            AIActionButton(
                isLoading = isLoading,
                onClick = {
                    if (!isSubscribed && freeAttempts == 0) {
                        Toast.makeText(context, context.getString(pl.parfen.blockappstudyrelease.R.string.subscription_required), Toast.LENGTH_SHORT).show()
                        return@AIActionButton
                    }
                    if (!isSubscribed) freeAttempts--
                    aiResponse = ""
                    isLoading = true

                    val selectedLanguageCode = if (useOnlySecondLanguage)
                        languageCodes.getOrElse(selectedAdditionalLanguageIndex) { languageCodes[0] }
                    else
                        languageCodes.getOrElse(selectedLanguageIndex) { languageCodes[0] }

                    val filteredTopics = if (selectedTopicsState.isNotEmpty()) {
                        userTopicsState.value.filter { selectedTopicsState.contains(it.original) }
                    } else {
                        userTopicsState.value
                    }

                    val selectedTopic = filteredTopics.randomOrNull()?.original ?: "Default"
                    val prompt = createPrompt(context, age, selectedTopic, selectedLanguageCode, languageCodes, breakIntoSyllables)

                    chatGPTManager.getChatGPTResponse(prompt, "gpt-3.5-turbo", false) { result ->
                        aiResponse = result
                        isLoading = false
                    }
                }
            )

            AIResponseArea(
                aiResponse = aiResponse,
                isLoading = isLoading,
                lazyListState = lazyListState
            )
        }

        AIBottomActions(
            context = context,
            profileId = profileId,
            aiNetwork = aiNetwork,
            userTopics = userTopicsState.value,
            aiLanguage = aiLanguage,
            selectedTopics = selectedTopicsState,
            additionalLanguage = languageNames.getOrNull(selectedAdditionalLanguageIndex)
        )
    }
}
