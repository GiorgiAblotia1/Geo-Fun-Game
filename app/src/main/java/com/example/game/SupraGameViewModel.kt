package com.example.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

// --- Enums ---
enum class ToastType(val displayName: String) {
    SHORT("მოკლე და პირდაპირი"),
    PHILOSOPHICAL("ღრმა ფილოსოფიური"),
    POETIC("პოეტურ-ემოციური"),
    AI_GENERATED("AI სადღეგრძელო (Gemini)")
}

enum class DrinkStyle(val displayName: String) {
    BOTTOMS_UP("ბოლომდე დალევა (ჭიქით)"),
    FAKE_DRINK("მოყუდება / მოტყუება"),
    HORN("ყანწით ბოლომდე დალევა")
}

enum class FoodType(val displayName: String, val cost: Int, val description: String) {
    KHACHAPURI("აჭარული ხაჭაპური", 25, "ამცირებს სიმთვრალეს 10%-ით, ნოყიერია"),
    KHINKALI_HANDS("ხინკალი (ხელით)", 15, "ნამდვილი ვაჟკაცური ჭამა. პატივისცემა +5"),
    KHINKALI_FORK("ხინკალი (ჩანგლით!)", 15, "მკრეხელობა! სუფრის წევრების გაკვირვება"),
    MTSVADI("ღორის მწვადი", 30, "გემრიელი და ცხიმიანი. სიმთვრალეს ანელებს"),
    PICKLES("ჯონჯოლი / მწნილი", 5, "მცირე საჭმელი. ამცირებს სიმთვრალეს 5%-ით")
}

enum class ActivityType(val displayName: String) {
    DANCE("ქართული ცეკვა (აჭარული)"),
    RESTROOM("ფეხზე გასვლა / ჰაერზე"),
    SNEAK_OUT("ჩუმად გაპარვა სახლში")
}

// --- Data Classes ---
data class Character(
    val id: String,
    val name: String,
    val nickname: String,
    val description: String,
    val traitName: String,
    val traitDescription: String,
    val foodLimit: Int,
    val drunknessTolerance: Float,
    val initialRespect: Int,
    val initialDrunkness: Int,
    val initialFullness: Int
)

data class SupraStage(
    val number: Int,
    val name: String,
    val description: String,
    val dialogText: String,
    val idealTopic: String
)

val CharactersList = listOf(
    Character(
        id = "guram",
        name = "ბიძა გურამი",
        nickname = "ღვიძლის ტერმინატორი",
        description = "სუფრის ვეტერანი, რომელსაც შეუძლია 3 ლიტრი საფერავი დალიოს თვალის დახამხამების გარეშე და სადღეგრძელოზე ისეთი ტირილი დაიწყოს, რომ მეზობლის კატაც კი აატიროს. მისი საყვარელი ფრაზაა: 'ჩვენს წინაპრებს გაუმარჯოს, თორემ დინოზავრებმა რა იცოდნენ ვაზი!'",
        traitName = "ცრემლისფრქვევა",
        traitDescription = "სადღეგრძელოებზე იღებს 25%-ით მეტ პატივისცემას, რადგან მისი ემოციურობით მეზობლებს გულებს უჩუყებს.",
        foodLimit = 100,
        drunknessTolerance = 0.9f,
        initialRespect = 60,
        initialDrunkness = 5,
        initialFullness = 15
    ),
    Character(
        id = "jimi",
        name = "მეზობელი ჯიმი",
        nickname = "კოსმოსური ტამპლიერი",
        description = "ყველაზე ხმაურიანი მეზობელი. მესამე ჭიქის შემდეგ იწყებს მტკიცებას, რომ მისი წინაპრები შუმერი უფოლოგები იყვნენ და აჭარულ ცეკვას პირველად ის დანერგავს მარსზე. მისი დანახვისას ყველა ხელადას მალავს.",
        traitName = "ყანწის ჰიპნოზი",
        traitDescription = "ყანწით დალევისას სიმთვრალე ეზრდება 20%-ით ნაკლებად, ხოლო პატივისცემა - 50%-ით მეტად.",
        foodLimit = 100,
        drunknessTolerance = 1.0f,
        initialRespect = 50,
        initialDrunkness = 0,
        initialFullness = 10
    ),
    Character(
        id = "nika",
        name = "ნიკა",
        nickname = "ხინკლის მტაცებელი",
        description = "ახალგაზრდა, რომელიც სუფრაზე მხოლოდ ხინკლის საჭმელად მოდის. მისი პირადი რეკორდია 73 ხინკალი ერთ ჯერზე, რის გამოც უბანში 'კუჭის ვირტუოზი' შეარქვეს. ჩანგლის დანახვაზე ისტერიული კანკალი ეწყება.",
        traitName = "ხინკლის შავი ხვრელი",
        traitDescription = "საჭმელი სიმთვრალეს 50%-ით მეტად უმცირებს, ხოლო დანაყრების ლიმიტი უზარმაზარი აქვს.",
        foodLimit = 180,
        drunknessTolerance = 1.0f,
        initialRespect = 45,
        initialDrunkness = 0,
        initialFullness = 20
    ),
    Character(
        id = "natela",
        name = "დეიდა ნათელა",
        nickname = "საგარეო ჭორების სამინისტრო",
        description = "უბნის მთავარი ცოცხალი რადიო და ჭორების ენციკლოპედია. იცის ყველას ხელფასი, ვინ ვის გაჰყვა, რატომ არის დღევანდელი ხაჭაპური ოდნავ მარილიანი და ვინ აპირებს სახლის გაყიდვას. თუ ჭორს არ მოუსმენ, 'უზდელად' გამოგაცხადებს.",
        traitName = "ჭორბიუროს იმუნიტეტი",
        traitDescription = "ჭიქის მოტყუება (Fake Drink) ყოველთვის წარმატებულია, რადგან მისი ჭორების მოსმენით ყველა გაოგნებულია! თუმცა საჭმლის ლიმიტი დაბალია.",
        foodLimit = 70,
        drunknessTolerance = 1.1f,
        initialRespect = 65,
        initialDrunkness = 0,
        initialFullness = 10
    ),
    Character(
        id = "vano",
        name = "ტაქსისტი ვანო",
        nickname = "ფხიზელი შუმახერი",
        description = "მოვიდა გაზზე მომუშავე Opel Astra-თი, სულ ეჩქარება, მაგრამ სუფრიდან არავინ უშვებს. ყოველ წამს ყვირის: 'გზებზე საცობია, მანქანა ორმაგად მიყენია, უნდა გავიქცე!', თუმცა მეხუთე მწვადს ჭამს და პოლიტიკაზე ლექციებს კითხულობს.",
        traitName = "ოპელ ასტრას ძალა",
        traitDescription = "ალკოჰოლი 50%-ით ნაკლებად მოქმედებს (სიმთვრალე ნახევრდება), თუმცა ბოლომდე დალევის იძულება პატივისცემას უმცირებს.",
        foodLimit = 100,
        drunknessTolerance = 0.5f,
        initialRespect = 50,
        initialDrunkness = 0,
        initialFullness = 15
    ),
    Character(
        id = "vaja",
        name = "თამადა ვაჟა",
        nickname = "რიტორიკის დიდოსტატი",
        description = "პროფესიონალი თამადა, რომელიც სადღეგრძელოებს ისეთი პათოსით ამბობს, თითქოს კონსტიტუციის პრეამბულას კითხულობდეს. Fake Drink-ის გაკეთებას თუ შეეცდები, თვალებიდან ლაზერებს გესვრის და სუფრიდან მყისიერად მოგკვეთს.",
        traitName = "აკადემიური სიტყვა",
        traitDescription = "სადღეგრძელოები აძლევს 40%-ით მეტ პატივისცემას. თუმცა, Fake Drink (მოტყუება) მისთვის აკრძალულია - დაჭერისას პატივისცემა მყისიერად 0 ხდება!",
        foodLimit = 110,
        drunknessTolerance = 1.0f,
        initialRespect = 75,
        initialDrunkness = 10,
        initialFullness = 20
    )
)

val SupraStagesList = listOf(
    SupraStage(
        number = 1,
        name = "მშვიდობის",
        description = "პირველი სადღეგრძელო, სადაც თამადა 40 წუთი საუბრობს კაცობრიობის ბედზე, სანამ შენი ხელი ჭიქის ჭერით კანკალს იწყებს.",
        dialogText = "თამადა წამოდგა, სახე ისე გაასერიოზულა, თითქოს გაეროს ტრიბუნაზე იდგეს: 'მეგობრებო! პირველი ჭიქით ჩვენს გალაქტიკაში, ჩვენს უბანში და განსაკუთრებით ამ ეზოში მშვიდობას გაუმარჯოს! მშვიდობა იყოს, თორემ მეზობელი ლიანა ისევ გვიჩივლებს ხმაურზე!'",
        idealTopic = "მშვიდობა"
    ),
    SupraStage(
        number = 2,
        name = "მასპინძლის",
        description = "უნდა დაამტკიცო, რომ მასპინძლის დაწურული ღვინო ფრანგულ შამპანურს სჯობს, თუნდაც ძმრისა და აცეტონის გემო დაკრავდეს.",
        dialogText = "თამადამ მასპინძელს მხარზე ხელის დარტყმით კინაღამ ნეკნი მოტეხა: 'ამ ოჯახის კერას და მის ოქროს მასპინძელს გაუმარჯოს! განსაკუთრებით მის ხელნაკეთ ღვინოს, რომელიც გემოთი ფრანგულ შატოებს ხევს, ხოლო დილით თავის ტკივილით კოსმოსში გაგაფრენს!'",
        idealTopic = "მასპინძელი"
    ),
    SupraStage(
        number = 3,
        name = "მშობლების",
        description = "უაღრესად სენტიმენტალური წამი. სუფრის ნახევარი ცხარე ცრემლით ტირის, ხოლო მეორე ნახევარი ჩუმად მწვადს ეპარება.",
        dialogText = "მუსიკა ჩაწყდა, ბიძა გურამმა ხელსახოცით თვალების მშრალება დაიწყო: 'მშობლებს გაუმარჯოს, ვინც სიცოცხლე და პირველი ხინკლის რეცეპტი მოგვცა! იმათ ჯანმრთელობას, ვინც დღემდე გვირეკავს და გვეკითხება: ჭამე შვილო?!'",
        idealTopic = "მშობლები"
    ),
    SupraStage(
        number = 4,
        name = "სამშობლოს",
        description = "პატრიოტული პიკი! თამადა ისეთი ხმით ყვირის, რომ მეზობელ კორპუსებში მანქანის სიგნალიზაციები ირთვება.",
        dialogText = "ყველა ფეხზე წამოდგა. თამადამ ხმამაღლა დაიჭექა: 'ჩვენს სამშობლოს გაუმარჯოს! ჩვენს ქვის ციხესიმაგრეებს, ჩვენს მთებს და იმ დიდებულ ფაქტს, რომ ხინკალი ჩვენი გამოგონილია და არა სხვისი! უფალი ფარავდეს საქართველოს!'",
        idealTopic = "სამშობლო"
    ),
    SupraStage(
        number = 5,
        name = "სიყვარულის",
        description = "ჯიმი გიტარაზე უკრავს ძველ მელოდიას, რომელიც მხოლოდ ორი აკორდისგან შედგება, მაგრამ თავს მოცარტად მიიჩნევს.",
        dialogText = "ჯიმიმ გიტარა მოიმარჯვა, თვალები დახუჭა და ურიტმოდ დაჰკრა სიმებს. თამადამ გაიღიმა: 'სიყვარულს გაუმარჯოს! იმ გრძნობას, რომელიც გაიძულებს 3-ჯერ შემოუარო გოგოს კორპუსს Opel Astra-თი და სიყვარულისგან საბურავები დაწვა!'",
        idealTopic = "სიყვარული"
    ),
    SupraStage(
        number = 6,
        name = "მოგონების",
        description = "სერიოზული წუთი, სადაც ყველა წყნარდება, მაგრამ ნიკა მაინც ახერხებს ჩუმად ხინკლის კუჭების გადაყლაპვას.",
        dialogText = "სიჩუმე ჩამოწვა. თამადამ ჭიქა ნელა ასწია: 'იმ დიდებულ წინაპრებსა და ადამიანებს გაუმარჯოს, ვინც ჩვენს გულებში ცხოვრობს. ნათელში იყვნენ... ახლა კი, ნიკა, ჩაიდგი ეგ ხინკალი პირში და ნუ ღეჭავ ხმამაღლა!'",
        idealTopic = "მოგონება"
    ),
    SupraStage(
        number = 7,
        name = "მეზობლის ძაღლის (მურზას)",
        description = "პირველი ექსცენტრიული გამოცდა. ბიძა გურამს თავისი მეცხვარე ნაგაზი შვილზე მეტად უყვარს.",
        dialogText = "ბიძა გურამმა მაგიდაზე ჩანგალი დაარტყა: 'ხალხო! განსაკუთრებული სადღეგრძელო უნდა შესვათ... ჩემი მურზასი! გუშინ კატას ისე ყეფით გაეკიდა, რომ ჩემი გული მოიგო! ვინც მურზას სადღეგრძელოს არ დალევს, ის ოპელის საბარგულში გაათენებს ღამეს!'",
        idealTopic = "ფაუნა და მურზა"
    ),
    SupraStage(
        number = 8,
        name = "კოსმოსური საქართველოს",
        description = "მე-3 ლიტრი საფერავის შემდეგ, მარსზე ქვევრების ჩაფლვისა და ილონ მასკის გაქართველების იუმორისტული ეტაპი.",
        dialogText = "ჯიმიმ, რომელმაც თვალები ვეღარ გაახილა, ხელი ცისკენ გაიშვირა: 'ბიჭო! მარსზე რომ ავალთ, პირველ რიგში კოსმოსურ ქვევრს ჩავფლავთ, მწვადს კოსმოსურ შამფურზე შევწვავთ და უცხოპლანეტელებს 'რაჭულს' ვაცეკვებთ! აი ამ კოსმოსურ მომავალს გაუმარჯოს!'",
        idealTopic = "კოსმოსი და მარსი"
    ),
    SupraStage(
        number = 9,
        name = "საგვარეულო ჭორების",
        description = "დეიდა ნათელა გკითხავს შორეულ ნათესავებზე. თუ შეგეშლება, მემკვიდრეობის ნაცვლად მხოლოდ ძველ ნათურას მიიღებ.",
        dialogText = "დეიდა ნათელამ თვალები მოჭუტა, 2-ლიტრიანი ხელადა დაგაჭერინა: 'აბა, შვილო, მეორე მამიდის მესამე ქმრის ბიძაშვილის ნახევარძმა - ელგუჯა თუ იცი?! ვერ იცნობ, აბა რა, სულ ტელეფონებში ხართ შეძვრალები! ახლა ამ საგვარეულოს ჭორების სადღეგრძელო შესვი ბოლომდე, თორემ მთელ უბანს ვეტყვი, რომ ჩანგლით ჭამ ხინკალს!'",
        idealTopic = "ნათესავები და ჭორები"
    ),
    SupraStage(
        number = 10,
        name = "ირმის ყანწის ოლიმპიადის",
        description = "1.5-ლიტრიანი ირმის რქის ფორმის მონსტრი სასმისი, საიდანაც სმის დროს ყურებიდან ორთქლი გამოგდის.",
        dialogText = "თამადამ გამომცდელად ჩაგხედა თვალებში და უზარმაზარი ირმის ყანწი გადმოგცა: 'აბა, ვაჟკაცო! ეს ყანწი ბოლომდე უნდა დაცალო, თან ისე, რომ წვეთი არ დაგივარდეს და ბოლოს მაგიდაზე ხმამაღლა დაარტყა! უარს თუ იტყვი, ხვალიდან 'ჩაის მსმელს' შეგარქმევთ!'",
        idealTopic = "გმირობა და ყანწები"
    ),
    SupraStage(
        number = 11,
        name = "ბოლო ჭიქის (დედამიწის გადარჩენის)",
        description = "ყველაფერი 360 გრადუსით ტრიალებს. კოსმოსური ხომალდები მრავალჟამიერს მღერიან და თამადა ბოლო წვეთს გთხოვს.",
        dialogText = "მაგიდა ჰაერში დაფრინავს, ბიძა გურამს თავზე ჰალო დაადგა, ხოლო მასპინძელი გეუბნება: 'ეს არის ბოლო, უზენაესი სადღეგრძელო! ამ ჭიქას ვინც ბოლომდე დალევს, მასზე ბენზინის ფასის ზრდა და მეზობლის კორპუსის ჭორები ვეღარასდროს იმოქმედებს! დავლიოთ დედამიწის გადასარჩენად!'",
        idealTopic = "უზენაესი სამყარო"
    )
)

// --- Screen States ---
enum class GameScreen {
    HOME, CHARACTER_SELECTION, GAMEPLAY, GAME_OVER
}

// --- Ending Types ---
enum class GameEnding(
    val title: String,
    val description: String,
    val isWin: Boolean
) {
    WINE_LEGEND(
        "სუფრის უზენაესი იმპერატორი და ყანწის მბრძანებელი! 👑",
        "შენ წარმატებით დაძლიე 11-ვე ეტაპი, დალიე კოსმიური რაოდენობის საფერავი და ხინკალი ხელით ჭამე! ბიძა გურამმა თავისი ძველი მერსედესის გასაღები გაჩუქა, ჯიმიმ მარსის გუბერნატორად გამოგაცხადა, ხოლო დეიდა ნათელამ აღიარა, რომ მისი საყვარელი ნათესავი ხარ! შენზე ლეგენდები დაიწერება!",
        true
    ),
    PASSED_OUT(
        "მურზასთან ჩახუტებული ძილი 🐕",
        "სიმთვრალემ 100%-ს მიაღწია. შენს თვალებში შუქი ჩაქრა, თავი პირდაპირ საცივის თეფშში ჩარგე და გმირულად ჩაგეძინა. დილით გაიღვიძე ეზოში, მეზობლის ძაღლ მურზასთან ჩახუტებულმა. შუბლზე პომადით ყანწი გაქვს დახატული, ხოლო ჯიბეში გაურკვეველი ხინკლის კუჭები გიპოვეს...",
        false
    ),
    BANISHED(
        "სირცხვილის კორიდორით გაძევებული! 🚪",
        "პატივისცემა 0%-ია. სუფრის წევრებმა 'ლიმონათის მსმელი' და 'ტრადიციების მტერი' შეგარქვეს, ზურგზე ხაჭაპურის ნამცეცები დაგაყარეს და სირცხვილის კორიდორით გაგაძევეს! ტაქსისტმა ვანომაც კი უარი თქვა შენს წაყვანაზე: 'მანქანაში მაგ უზდელს არ ჩავისვამ, გაზის ბალონი ამიფეთქდებაო'!",
        false
    ),
    FORK_DISASTER(
        "ხინკლის ჩანგლით მკვლელობა 🍴",
        "ვაი შენს დღეს! ხინკალი ჩანგლით გახვრიტე! ძვირფასი, წმინდა წვენი თეფშზე უსულოდ გადმოიღვარა. მთელი სუფრა წამოდგა, ჯიმიმ გიტარა თავზე გადაგამტვრია, ხოლო ბიძა გურამმა საერთოდ უარგყო შენი არსებობა. საავადმყოფოში გაიღვიძე, სადაც ექიმმაც კი გკითხა: 'ჩანგლით ხინკალს როგორ ხვრეტდი, შე კაჟო შენა?!'",
        false
    ),
    RESPONSIBLE_DRIVER(
        "გამპარავი ჩუმჩუმელა 🚗",
        "შენ წარმატებით გაიპარე სუფრიდან ფხიზელი. სახლში მშვიდობიანად მიხვედი, მაგრამ მეორე დღეს უბანში ყველა 'გამპარავს' და 'ლიმონათის იმპერატორს' გეძახის. ჯიმიმ 'ფეისბუქზე' დაგბლოკა, ხოლო დეიდა ნათელამ გადმოგცა, რომ მემკვიდრეობიდან ოფიციალურად ამოგშალა!",
        true
    )
}

// --- Active Random Scenario ---
data class TableEvent(
    val title: String,
    val description: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val onChoice: (Int) -> String
)

// --- Full Game State ---
data class GameState(
    val currentScreen: GameScreen = GameScreen.HOME,
    val selectedCharacter: Character? = null,
    val respect: Int = 50,
    val drunkness: Int = 0,
    val fullness: Int = 0,
    val currentStageIndex: Int = 0,
    val historyLog: List<String> = emptyList(),
    val isAILoading: Boolean = false,
    val generatedAIToast: String? = null,
    val activeEvent: TableEvent? = null,
    val activeToastLog: String? = null,
    val activeActionCategory: String = "TOAST", // TOAST, DRINK, EAT, ACTIVITY
    val ending: GameEnding? = null,
    val roundsCompleted: Int = 0,
    val cheatAttempts: Int = 0
)

class SupraGameViewModel : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    // Characters definition
    val characters = CharactersList

    // Stages definition
    val stages = SupraStagesList

    fun changeScreen(screen: GameScreen) {
        _state.update { it.copy(currentScreen = screen) }
    }

    fun selectCharacter(character: Character) {
        SupraSoundManager.playClink()
        _state.update {
            it.copy(
                selectedCharacter = character,
                respect = character.initialRespect,
                drunkness = character.initialDrunkness,
                fullness = character.initialFullness,
                currentScreen = GameScreen.GAMEPLAY,
                historyLog = listOf("სუფრა დაიწყო! შენ შეხვედი როგორც ${character.name} (${character.nickname}).")
            )
        }
        triggerStageIntro()
    }

    private fun addLog(message: String) {
        _state.update {
            it.copy(historyLog = listOf(message) + it.historyLog)
        }
    }

    private fun triggerStageIntro() {
        val s = _state.value
        val stage = stages.getOrNull(s.currentStageIndex) ?: return
        addLog("--- ეტაპი ${stage.number}: ${stage.name} სადღეგრძელო ---")
        addLog(stage.dialogText)
    }

    fun setActionCategory(category: String) {
        _state.update { it.copy(activeActionCategory = category) }
    }

    // --- Core Action: Toast ---
    fun selectToast(type: ToastType, customTopic: String = "") {
        val char = _state.value.selectedCharacter ?: return
        val stage = stages.getOrNull(_state.value.currentStageIndex) ?: return

        SupraSoundManager.playClink()
        _state.update { it.copy(isAILoading = true, activeToastLog = null, generatedAIToast = null) }

        viewModelScope.launch {
            val toastText = when (type) {
                ToastType.SHORT -> "მოკლედ ვიტყვი, ბევრი ლაპარაკი მე არ მიყვარს. ამ ეტაპს გაუმარჯოს, ღმერთმა ბედნიერება და ჯანმრთელობა მოგვცეს ყველას! გაუმარჯოს!"
                ToastType.PHILOSOPHICAL -> "ცხოვრება ჰგავს ვენახს, მეგობრებო... თუ არ მოუარე, არ მოეფერე, კარგ მოსავალს ვერ მიიღებ. მოდით, ჩვენს ${stage.idealTopic.lowercase()}ს ისე მოვუაროთ და მოვეფეროთ, რომ ცხოვრების ბოლოს საუკეთესო ღვინო შეგვრჩეს ხელში! გაუმარჯოს!"
                ToastType.POETIC -> "ოჰ, რა ლამაზია დღევანდელი საღამო! სიტყვები არ მყოფნის, რომ გამოვხატო ის სითბო, რასაც აქ ვგრძნობ. როგორც ნიავი ეფერება ალაზნის ველს, ისე ჩემს გულს ეფერება თქვენი პატივისცემა. ამ შესანიშნავ ${stage.idealTopic.lowercase()}ს გაუმარჯოს, გულით და სულით!"
                ToastType.AI_GENERATED -> {
                    val topic = customTopic.ifBlank { stage.idealTopic }
                    GeminiClient.generateToast(char.name, topic, stage.name)
                }
            }

            // Calculate gains
            var respectGain = when (type) {
                ToastType.SHORT -> 5
                ToastType.PHILOSOPHICAL -> 15
                ToastType.POETIC -> 20
                ToastType.AI_GENERATED -> 25
            }

            // Character Trait modifiers
            if (char.id == "guram") {
                respectGain = (respectGain * 1.15f).toInt()
            }
            if (char.id == "vaja") {
                respectGain = (respectGain * 1.40f).toInt()
            }

            _state.update { s ->
                val newRespect = (s.respect + respectGain).coerceIn(0, 100)
                s.copy(
                    isAILoading = false,
                    generatedAIToast = toastText,
                    respect = newRespect,
                    activeToastLog = "შენ წარმოთქვი სადღეგრძელო: \"$toastText\" (პატივისცემა +$respectGain)"
                )
            }
            addLog("შენ წარმოთქვი ${type.displayName} სადღეგრძელო! სუფრამ ტაში დაგიკრა.")
            checkGameConditions()
        }
    }

    // --- Core Action: Drink ---
    fun drinkWine(style: DrinkStyle) {
        val s = _state.value
        val char = s.selectedCharacter ?: return
        val stage = stages.getOrNull(s.currentStageIndex) ?: return

        SupraSoundManager.playClink()
        var drunknessIncrease = when (style) {
            DrinkStyle.BOTTOMS_UP -> 20
            DrinkStyle.FAKE_DRINK -> 5
            DrinkStyle.HORN -> 40
        }

        var respectGain = when (style) {
            DrinkStyle.BOTTOMS_UP -> 15
            DrinkStyle.FAKE_DRINK -> -5
            DrinkStyle.HORN -> 30
        }

        // Apply tolerances
        drunknessIncrease = (drunknessIncrease * char.drunknessTolerance).toInt()

        // Character Trait modifiers
        if (char.id == "jimi" && style == DrinkStyle.HORN) {
            respectGain = (respectGain * 1.5f).toInt()
            drunknessIncrease = (drunknessIncrease * 0.8f).toInt()
        }

        if (char.id == "vano") {
            if (style == DrinkStyle.HORN || style == DrinkStyle.BOTTOMS_UP) {
                respectGain = (respectGain * 0.5f).toInt()
            }
        }

        if (char.id == "nika") {
            // Nika got 50% more food defense
            val foodDefense = (s.fullness * 0.2f).toInt()
            drunknessIncrease = (drunknessIncrease - foodDefense).coerceAtLeast(2)
        } else {
            val foodDefense = (s.fullness * 0.1f).toInt()
            drunknessIncrease = (drunknessIncrease - foodDefense).coerceAtLeast(2)
        }

        // Special handling for cheating
        var logMessage = ""
        var isCaught = false
        if (style == DrinkStyle.FAKE_DRINK) {
            val catchChance = when (char.id) {
                "natela" -> 5
                "vaja" -> 100
                else -> 40 + (s.cheatAttempts * 15) // harder each time
            }
            if (Random.nextInt(100) < catchChance) {
                isCaught = true
                if (char.id == "vaja") {
                    respectGain = -50
                    drunknessIncrease = (25 * char.drunknessTolerance).toInt()
                    logMessage = "სკანდალი! თამადა ვაჟამ ჭიქა მოატყუა! მთელმა სუფრამ დაინახა და შეძრწუნდა: 'თამადა როგორ კადრულობს ამას?!' - და ძალით დაალევინეს დიდი საჯარიმო! (პატივისცემა -50, სიმთვრალე +$drunknessIncrease)"
                } else {
                    respectGain = -25
                    drunknessIncrease = (25 * char.drunknessTolerance).toInt() // Forced penalty drink
                    logMessage = "ვაი! მეზობელმა ჯიმიმ დაგინახა როგორ მოატყუე და ღვინო გადააქციე! 'ბიჭო, გვაკადრებ?! გვეთამაშები?!' - იყვირა მან და ძალით დაგალევინა საჯარიმო სასმისი! (პატივისცემა -25, სიმთვრალე +$drunknessIncrease)"
                }
                _state.update { it.copy(cheatAttempts = it.cheatAttempts + 1) }
            } else {
                logMessage = "წარმატებით მოატყუე! ჭიქა ტუჩებთან მიიტანე, მაგრამ ჩუმად გადაასხი იატაკზე. ვერავინ შეამჩნია! (სიმთვრალე +$drunknessIncrease)"
                _state.update { it.copy(cheatAttempts = it.cheatAttempts + 1) }
            }
        } else {
            logMessage = "შენ დალიე ${style.displayName}! (სიმთვრალე +$drunknessIncrease, პატივისცემა +$respectGain)"
        }

        _state.update { state ->
            val newDrunk = (state.drunkness + drunknessIncrease).coerceIn(0, 100)
            val newRespect = (state.respect + respectGain).coerceIn(0, 100)
            state.copy(
                drunkness = newDrunk,
                respect = newRespect,
                activeToastLog = null,
                generatedAIToast = null
            )
        }

        addLog(logMessage)

        // After drinking, progress to next stage!
        progressStage()
    }

    // --- Core Action: Eat ---
    fun eatFood(food: FoodType) {
        val s = _state.value
        val char = s.selectedCharacter ?: return

        if (s.fullness >= char.foodLimit) {
            addLog("ვერ ჭამ! კუჭი სავსე გაქვს, ვეღარაფერს ჩაიტევ!")
            return
        }

        if (food == FoodType.KHINKALI_FORK) {
            _state.update { state ->
                state.copy(
                    ending = GameEnding.FORK_DISASTER,
                    currentScreen = GameScreen.GAME_OVER
                )
            }
            return
        }

        var respectChange = 0
        var drunknessDecrease = 0

        when (food) {
            FoodType.KHACHAPURI -> {
                drunknessDecrease = 12
                respectChange = 2
            }
            FoodType.KHINKALI_HANDS -> {
                drunknessDecrease = 8
                respectChange = 5
            }
            FoodType.MTSVADI -> {
                drunknessDecrease = 15
                respectChange = 3
            }
            FoodType.PICKLES -> {
                drunknessDecrease = 5
                respectChange = 0
            }
            else -> {}
        }

        val foodGain = food.cost
        val newFullness = (s.fullness + foodGain).coerceAtMost(char.foodLimit)
        val newDrunkness = (s.drunkness - drunknessDecrease).coerceAtLeast(0)
        val newRespect = (s.respect + respectChange).coerceIn(0, 100)

        _state.update { state ->
            state.copy(
                fullness = newFullness,
                drunkness = newDrunkness,
                respect = newRespect
            )
        }

        addLog("შენ შეჭამე ${food.displayName}. (დანაყრება +$foodGain%, სიმთვრალე -$drunknessDecrease%, პატივისცემა +$respectChange)")
        checkGameConditions()
    }

    // --- Core Action: Activity ---
    fun performActivity(activity: ActivityType) {
        val s = _state.value
        val char = s.selectedCharacter ?: return

        when (activity) {
            ActivityType.DANCE -> {
                // Dance reflex game or choice
                val success = Random.nextInt(100) < 80
                if (success) {
                    SupraSoundManager.playClink()
                    val respectGain = 15
                    val drunkReduce = 10
                    val fullReduce = 25
                    _state.update { state ->
                        state.copy(
                            respect = (state.respect + respectGain).coerceIn(0, 100),
                            drunkness = (state.drunkness - drunkReduce).coerceAtLeast(0),
                            fullness = (state.fullness - fullReduce).coerceAtLeast(0)
                        )
                    }
                    addLog("სუფრაზე 'აჭარული' მუსიკა ჩაირთო! შენ საოცრად იცეკვე, ილეთები აკეთე და მთელი სუფრა აღაფრთოვანე. ჯიმიმ დოლზე დაგიკრა! (პატივისცემა +$respectGain, სიმთვრალე -$drunkReduce%, დანაყრება -$fullReduce%)")
                } else {
                    val respectLoss = 5
                    val fullReduce = 15
                    _state.update { state ->
                        state.copy(
                            respect = (state.respect - respectLoss).coerceAtLeast(0),
                            fullness = (state.fullness - fullReduce).coerceAtLeast(0)
                        )
                    }
                    addLog("სცადე ცეკვა, მაგრამ ფეხი ხაჭაპურის ნამცეცზე დაგიცურდა და კინაღამ მწვადების ლანგარში ჩავარდი. სუფრის წევრებმა იცინეს. (დანაყრება -$fullReduce%, პატივისცემა -$respectLoss)")
                }
            }
            ActivityType.RESTROOM -> {
                val drunkReduce = 15
                val gotChallenged = Random.nextInt(100) < 30
                if (gotChallenged) {
                    // Restroom corridor challenge!
                    _state.update { state ->
                        state.copy(
                            activeEvent = TableEvent(
                                title = "კორიდორის შეხვედრა",
                                description = "საპირფარეშოსკენ მიმავალს კორიდორში მასპინძლის ნასვამი ბიძაშვილი, ტარიელი შეგეფეთა. ხელში ორი სავსე ჭიქა უჭირავს: 'ოოო, ჩემს ძმას გაუმარჯოს! აქაც ჩვენი შეხვედრა?! მოდი, კორიდორული დავლიოთ ჩვენს მეგობრობას!'",
                                optionA = "დალიე ბოლომდე (სიმთვრალე +20, პატივისცემა +10)",
                                optionB = "უარი უთხარი თავაზიანად: 'ექიმი მიკრძალავს ძმაო' (პატივისცემა -10)",
                                optionC = "დაარწმუნე, რომ სუფრაზე ერთად დალევთ (საჭიროებს პატივისცემას > 40)",
                                onChoice = { choice ->
                                    when (choice) {
                                        0 -> {
                                            _state.update { state ->
                                                state.copy(
                                                    drunkness = (state.drunkness + (20 * char.drunknessTolerance).toInt()).coerceIn(0, 100),
                                                    respect = (state.respect + 10).coerceIn(0, 100),
                                                    activeEvent = null
                                                )
                                            }
                                            "ჩაეხუტე ტარიელს და ჭიქა გამოსცალე. ტარიელმა გულში ჩაგიკრა: 'კაცი ხარ შენ კაცი!'"
                                        }
                                        1 -> {
                                            _state.update { state ->
                                                state.copy(
                                                    respect = (state.respect - 10).coerceAtLeast(0),
                                                    activeEvent = null
                                                )
                                            }
                                            "ტარიელმა გულდაწყვეტით შემოგხედა: 'ეჰ, შენგან არ ველოდი ძმაო...' და მარტო დალია ჭიქა."
                                        }
                                        else -> {
                                            if (s.respect > 40) {
                                                _state.update { state ->
                                                    state.copy(
                                                        respect = (state.respect + 5).coerceIn(0, 100),
                                                        activeEvent = null
                                                    )
                                                }
                                                "ტარიელს მხარზე ხელი მოუთათუნე და დაარწმუნე, რომ სუფრაზე ყველას წინაშე მასთან ერთად განსაკუთრებულს დალევდი. ტარიელმა პატივისცემით თავი დაგიკრა და გაგიშვა."
                                            } else {
                                                _state.update { state ->
                                                    state.copy(
                                                        respect = (state.respect - 5).coerceAtLeast(0),
                                                        activeEvent = null
                                                    )
                                                }
                                                "ტარიელმა არ დაგიჯერა: 'არ გინდა ჩემთან დალევა და პირდაპირ თქვი!' - გაბრაზდა და ჭიქა მარტომ დალია."
                                            }
                                        }
                                    }
                                }
                            )
                        )
                    }
                    addLog("გახვედი ჰაერზე, მაგრამ კორიდორში ტარიელი გადაგეღობა!")
                } else {
                    _state.update { state ->
                        state.copy(
                            drunkness = (state.drunkness - drunkReduce).coerceAtLeast(0)
                        )
                    }
                    addLog("გახვედი ეზოში, ცივი წყალი შეისხი სახეზე, სუფთა ჰაერი ჩაყლაპე და ოდნავ გამოფხიზლდი. (სიმთვრალე -$drunkReduce%)")
                }
            }
            ActivityType.SNEAK_OUT -> {
                if (s.drunkness < 40) {
                    _state.update { state ->
                        state.copy(
                            ending = GameEnding.RESPONSIBLE_DRIVER,
                            currentScreen = GameScreen.GAME_OVER
                        )
                    }
                } else {
                    // Too drunk to sneak out, caught!
                    val drunknessIncrease = (25 * char.drunknessTolerance).toInt()
                    _state.update { state ->
                        state.copy(
                            respect = (state.respect - 15).coerceAtLeast(0),
                            drunkness = (state.drunkness + drunknessIncrease).coerceIn(0, 100)
                        )
                    }
                    addLog("ჩუმად ფეხაკრეფით მიდიოდი ჭიშკრისკენ, მაგრამ ნასვამობისგან ფეხი ქოთანს წამოჰკარი, რომელიც ხმაურით გატყდა. მასპინძელმა დაგინახა: 'სად გარბიხარ, კაცო, სუფრა ახლა იწყება!' - და ძალით დაგაბრუნა მაგიდასთან საჯარიმო ჭიქით ხელში! (პატივისცემა -15, სიმთვრალე +$drunknessIncrease%)")
                }
            }
        }
        checkGameConditions()
    }

    // --- Handle Scenario Choice ---
    fun selectEventChoice(choiceIndex: Int) {
        val s = _state.value
        val event = s.activeEvent ?: return
        val resultLog = event.onChoice(choiceIndex)
        addLog(resultLog)
        checkGameConditions()
    }

    // --- Progress Supra ---
    private fun progressStage() {
        _state.update { s ->
            val nextIndex = s.currentStageIndex + 1
            if (nextIndex >= stages.size) {
                // Game completed! Win ending!
                s.copy(
                    ending = GameEnding.WINE_LEGEND,
                    currentScreen = GameScreen.GAME_OVER,
                    roundsCompleted = stages.size
                )
            } else {
                s.copy(
                    currentStageIndex = nextIndex,
                    roundsCompleted = nextIndex,
                    activeToastLog = null,
                    generatedAIToast = null,
                    activeActionCategory = "TOAST"
                )
            }
        }

        val s = _state.value
        if (s.currentScreen == GameScreen.GAMEPLAY) {
            triggerStageIntro()
            // Randomly trigger an event at the start of stage (35% chance)
            if (Random.nextInt(100) < 35) {
                triggerRandomTableEvent()
            }
        }
    }

    // --- Trigger Random Event ---
    private fun triggerRandomTableEvent() {
        val char = _state.value.selectedCharacter ?: return
        val events = listOf(
            TableEvent(
                title = "ბებიას მზრუნველობა",
                description = "მასპინძლის ბებო ნაზი მოვიდა სუფრასთან, ხელში უზარმაზარი, ცხელი, ყველიანი იმერული ხაჭაპური უჭირავს და თეფშზე მერვე ნაჭერს გიდებს: 'ჭამე, შემოგევლოს ბებია, მთელი საღამოა არაფერი გიჭამია, რა გამხდარი ხარ!'",
                optionA = "შეჭამე დიდი მადლიერებით (დანაყრება +30, სიმთვრალე -15, პატივისცემა +10)",
                optionB = "უარი უთხარი თავაზიანად: 'აღარ შემიძლია ბებო, მართლა' (პატივისცემა -10)",
                optionC = "ჩუმად გადაუცურე თეფში მეზობელ ჯიმის (პატივისცემა +5)",
                onChoice = { choice ->
                    when (choice) {
                        0 -> {
                            _state.update { state ->
                                state.copy(
                                    fullness = (state.fullness + 30).coerceAtMost(char.foodLimit),
                                    drunkness = (state.drunkness - 15).coerceAtLeast(0),
                                    respect = (state.respect + 10).coerceIn(0, 100),
                                    activeEvent = null
                                )
                            }
                            "ბებო ნაზიმ თავზე გაკოცა: 'შენი ჭირიმე, რა ოქრო ბიჭი ხარ!' (დანაყრება +30, სიმთვრალე -15, პატივისცემა +10)"
                        }
                        1 -> {
                            _state.update { state ->
                                state.copy(
                                    respect = (state.respect - 10).coerceAtLeast(0),
                                    activeEvent = null
                                )
                            }
                            "ბებო ნაზიმ ამოიოხრა და თვალები აუცრემლიანდა: 'ეჰ, ჩემი გაკეთებული ხაჭაპური არ მოგეწონა, ალბათ კარგად ვერ გამოვაცხვე...' (პატივისცემა -10)"
                        }
                        else -> {
                            _state.update { state ->
                                state.copy(
                                    respect = (state.respect + 5).coerceIn(0, 100),
                                    activeEvent = null
                                )
                            }
                            "ჯიმის თეფში ჩუმად გადაუჩოჩე. ჯიმიმ თვალი ჩაგიკრა, ხაჭაპური ხელად გადაყლაპა და ბებოს უთხრა: 'ნაზი ბებო, საოცრებაა!' (პატივისცემა +5)"
                        }
                    }
                }
            ),
            TableEvent(
                title = "პოლიტიკური კამათი",
                description = "სუფრაზე მეზობლებმა ხმამაღალი კამათი დაიწყეს. ბიძა გურამი და მეზობელი ჯიმი ერთმანეთს უყვირიან და სულ მალე შეიძლება ჭიქები დაიმსხვრას! როგორ იქცევი?",
                optionA = "ჩაერთე და მხარი დაუჭირე გურამს (პატივისცემა +10 გურამთან, ჯიმისთან -15)",
                optionB = "წამოდექი და თქვი შერიგებისა და მშვიდობის სადღეგრძელო (სიმთვრალე +15, პატივისცემა +20)",
                optionC = "ჩუმად განაგრძე ჭამა და არ ჩაერიო (დანაყრება +10, სიმთვრალე -5)",
                onChoice = { choice ->
                    when (choice) {
                        0 -> {
                            _state.update { state ->
                                state.copy(
                                    respect = (state.respect - 10).coerceAtLeast(0),
                                    activeEvent = null
                                )
                            }
                            "შენ გურამს დაუჭირე მხარი. ჯიმი გაბრაზდა: 'შენ საერთოდ ვინ გკითხავს, პატარა ხარ ჯერ!' - სიტუაცია დაიძაბა. (პატივისცემა -10)"
                        }
                        1 -> {
                            _state.update { state ->
                                state.copy(
                                    respect = (state.respect + 20).coerceIn(0, 100),
                                    drunkness = (state.drunkness + (15 * char.drunknessTolerance).toInt()).coerceIn(0, 100),
                                    activeEvent = null
                                )
                            }
                            "წამოდექი, ჭიქა ასწიე და ისეთი შერიგების სიტყვა თქვი, რომ გურამმა და ჯიმიმ ერთმანეთს აკოცეს და ატირდნენ! (პატივისცემა +20, სიმთვრალე +15%)"
                        }
                        else -> {
                            _state.update { state ->
                                state.copy(
                                    fullness = (state.fullness + 10).coerceAtMost(char.foodLimit),
                                    drunkness = (state.drunkness - 5).coerceAtLeast(0),
                                    activeEvent = null
                                )
                            }
                            "კამათს ყური არ ათხოვე, ჩუმად მწვადის ლუკმა ჩაიდე პირში და საფერავი დააყოლე. (დანაყრება +10, სიმთვრალე -5)"
                        }
                    }
                }
            )
        )

        // Select a random event
        val randomEvent = events[Random.nextInt(events.size)]
        _state.update { it.copy(activeEvent = randomEvent) }
    }

    // --- Check Conditions ---
    private fun checkGameConditions() {
        val s = _state.value
        val char = s.selectedCharacter ?: return

        when {
            s.drunkness >= 100 -> {
                _state.update { it.copy(ending = GameEnding.PASSED_OUT, currentScreen = GameScreen.GAME_OVER) }
                addLog("გათიშვა! სიმთვრალე 100%-ია.")
            }
            s.respect <= 0 -> {
                _state.update { it.copy(ending = GameEnding.BANISHED, currentScreen = GameScreen.GAME_OVER) }
                addLog("გაძევება! პატივისცემა 0%-ია.")
            }
        }
    }

    // --- Restart Game ---
    fun restartGame() {
        _state.update {
            GameState(currentScreen = GameScreen.HOME)
        }
    }
}
