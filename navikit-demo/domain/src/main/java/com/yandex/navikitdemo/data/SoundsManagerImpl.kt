package com.yandex.navikitdemo.data

import android.content.Context
import android.content.res.AssetFileDescriptor
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.mapkit.annotations.SpeakerPhraseToken
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SoundsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.LinkedList
import java.util.Queue
import javax.inject.Inject

class SoundsManagerImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val settingsManager: SettingsManager,
) : SoundsManager {

    private companion object {
        const val OFFSET = 280L
    }

    private val scope = MainScope()

    private val assets = context.assets
    private val soundQueue: Queue<Pair<SpeakerPhraseToken, AssetFileDescriptor>> = LinkedList()
    private val soundDurations = mutableMapOf<String, Double>()

    init {
        settingsManager.annotationLanguage.changes()
            .onEach {
                clear()
                updateDurations()
            }
            .launchIn(scope)
        updateDurations()
    }

    override fun needUsePreRecorded(): Boolean =
        settingsManager.annotationLanguage.value in listOf(
            AnnotationLanguage.RUSSIAN,
            AnnotationLanguage.ENGLISH
        ) && settingsManager.preRecordedAnnotations.value

    override fun initPhrase(phrase: LocalizedPhrase): Boolean =
        try {
            soundQueue.clear()
            (if (phrase.tokens.map { it.path }
                    .contains(SpeakerPhraseToken.SPEED_LIMIT_EXCEEDED.path)) {
                phrase.tokens.map { it to assets.openFd("sounds/default/${SpeakerPhraseToken.SPEED_LIMIT_EXCEEDED.path}/0.mp3") }
            } else if (settingsManager.annotationLanguage.value == AnnotationLanguage.ENGLISH) {
                phrase.tokens.map { it to assets.openFd("sounds/en_male/${it.path}/0.mp3") }
            } else {
                phrase.tokens.map { it to assets.openFd("sounds/ru_female/${it.path}/0.mp3") }
            }).forEach {
                soundQueue.add(it)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    override fun clear() {
        soundQueue.clear()
    }

    override fun pollSoundFile(): Pair<SpeakerPhraseToken, AssetFileDescriptor>? =
        soundQueue.poll()

    override fun hasSoundFile(): Boolean =
        soundQueue.isNotEmpty()

    override fun getNextPlayDelay(phrase: SpeakerPhraseToken): Long {
        val duration = soundDurations.getOrDefault(phrase.path, 0).toFloat() * 1000
        return duration.toLong() - OFFSET
    }

    override fun updateDurations() {
        if (settingsManager.annotationLanguage.value !in listOf(
                AnnotationLanguage.RUSSIAN,
                AnnotationLanguage.ENGLISH
            )
        ) {
            soundDurations.clear()
            return
        }
        val json = try {
            val inputStream = when (settingsManager.annotationLanguage.value) {
                AnnotationLanguage.RUSSIAN -> assets.open("sounds/ru_female/durations.json")
                AnnotationLanguage.ENGLISH -> assets.open("sounds/en_male/durations.json")
                else -> return
            }
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        try {
            val jsonObject = JSONObject(json)
            SpeakerPhraseToken.values().map { it.path }.forEach { key ->
                if (jsonObject.has(key)) {
                    jsonObject.getJSONObject(key).let { keyObject ->
                        if (keyObject.has("0.mp3")) {
                            keyObject.getDouble("0.mp3").takeIf { it > 0 }?.let { value ->
                                soundDurations[key] = value
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

    private val SpeakerPhraseToken.path: String
        get() = when (this) {
            SpeakerPhraseToken.METER -> "Meter"
            SpeakerPhraseToken.METERS -> "Meters"
            SpeakerPhraseToken.METERS_2_4 -> "Meters2_4"
            SpeakerPhraseToken.KILOMETER -> "Kilometer"
            SpeakerPhraseToken.KILOMETERS -> "Kilometers"
            SpeakerPhraseToken.KILOMETERS_2_4 -> "Kilometers2_4"

            SpeakerPhraseToken.THEN -> "Then"
            SpeakerPhraseToken.AND -> "And"
            SpeakerPhraseToken.STRAIGHT -> "Forward"
            SpeakerPhraseToken.OVER -> "Over"
            SpeakerPhraseToken.EXIT -> "Exit"
            SpeakerPhraseToken.AHEAD -> "Ahead"
            SpeakerPhraseToken.ROUTE_FINISHED -> "RouteFinished"
            SpeakerPhraseToken.ROUTE_WILL_FINISH -> "RouteWillFinish"

            SpeakerPhraseToken.AFTER_BRIDGE -> "LandmarkAfterBridge"
            SpeakerPhraseToken.AFTER_TUNNEL -> "LandmarkAfterTunnel"
            SpeakerPhraseToken.AT_TRAFFIC_LIGHTS -> "LandmarkAtTrafficLights"
            SpeakerPhraseToken.BEFORE_BRIDGE -> "LandmarkBeforeBridge"
            SpeakerPhraseToken.BEFORE_TRAFFIC_LIGHTS -> "LandmarkBeforeTrafficLights"
            SpeakerPhraseToken.BEFORE_TUNNEL -> "LandmarkBeforeTunnel"
            SpeakerPhraseToken.INTO_COURTYARD -> "LandmarkIntoCourtyard"
            SpeakerPhraseToken.INTO_TUNNEL -> "LandmarkIntoTunnel"
            SpeakerPhraseToken.TO_BRIDGE -> "LandmarkToBridge"
            SpeakerPhraseToken.TO_FRONTAGE_ROAD -> "LandmarkToFrontageRoad"
            SpeakerPhraseToken.HARD_TURN_LEFT -> "HardTurnLeft"
            SpeakerPhraseToken.HARD_TURN_RIGHT -> "HardTurnRight"
            SpeakerPhraseToken.TAKE_LEFT -> "TakeLeft"
            SpeakerPhraseToken.TAKE_RIGHT -> "TakeRight"
            SpeakerPhraseToken.TURN_LEFT -> "TurnLeft"
            SpeakerPhraseToken.TURN_RIGHT -> "TurnRight"
            SpeakerPhraseToken.TURN_BACK -> "TurnBack"
            SpeakerPhraseToken.BOARD_FERRY -> "BoardFerry"
            SpeakerPhraseToken.ENTER_ROUNDABOUT -> "InCircularMovement"

            SpeakerPhraseToken.ROUTE_UPDATED -> "RouteRecalculated"
            SpeakerPhraseToken.ROUTE_UPDATED_TO_TOLL_ROAD -> "RouteUpdatedToTollRoad"
            SpeakerPhraseToken.GONE_OFF_ROUTE -> "RouteLost"
            SpeakerPhraseToken.RETURNED_ON_ROUTE -> "RouteReturn"
            SpeakerPhraseToken.SPEED_LIMIT_EXCEEDED -> "Danger"
            SpeakerPhraseToken.WAY_POINT_PASSED -> "RouteViaPoint"
            SpeakerPhraseToken.FASTER_ROUTE_AVAILABLE -> "FasterRouteAvailable"
            SpeakerPhraseToken.DANGER -> "DangerousRoads"

            SpeakerPhraseToken.ACCIDENT -> "Accident"
            SpeakerPhraseToken.RECONSTRUCTION -> "Reconstruction"
            SpeakerPhraseToken.LANE_CAMERA -> "LaneCamera"
            SpeakerPhraseToken.SPEED_CAMERA -> "SpeedCamera"
            SpeakerPhraseToken.CAMERA -> "SpeedCamera"
            SpeakerPhraseToken.ROAD_MARKING_CAMERA -> "RoadMarkingCamera"
            SpeakerPhraseToken.CROSS_ROAD_CAMERA -> "CrossRoadCamera"
            SpeakerPhraseToken.FORBIDDEN_STOP_CAMERA -> "ForbiddenStopCamera"
            SpeakerPhraseToken.MOBILE_CAMERA -> "MobileCamera"
            SpeakerPhraseToken.SPEED_LIMIT_CAMERA -> "SpeedCamera"

            SpeakerPhraseToken.AT_MIDDLE -> "AtMiddle"
            SpeakerPhraseToken.AT_LEFT -> "AtLeft"
            SpeakerPhraseToken.AT_RIGHT -> "AtRight"
            SpeakerPhraseToken.AND_MIDDLE -> "AndMiddle"
            SpeakerPhraseToken.AND_RIGHT -> "AndRight"
            SpeakerPhraseToken.LANE_LOCATIVE -> "Row"

            SpeakerPhraseToken.SPEED30 -> "Speed30"
            SpeakerPhraseToken.SPEED40 -> "Speed40"
            SpeakerPhraseToken.SPEED50 -> "Speed50"
            SpeakerPhraseToken.SPEED60 -> "Speed60"
            SpeakerPhraseToken.SPEED70 -> "Speed70"
            SpeakerPhraseToken.SPEED80 -> "Speed80"
            SpeakerPhraseToken.SPEED90 -> "Speed90"
            SpeakerPhraseToken.SPEED100 -> "Speed100"
            SpeakerPhraseToken.SPEED110 -> "Speed110"
            SpeakerPhraseToken.SPEED120 -> "Speed120"
            SpeakerPhraseToken.SPEED130 -> "Speed130"

            SpeakerPhraseToken.ONE -> "1"
            SpeakerPhraseToken.TWO -> "2"
            SpeakerPhraseToken.THREE -> "3"
            SpeakerPhraseToken.FOUR -> "4"
            SpeakerPhraseToken.FIVE -> "5"
            SpeakerPhraseToken.SIX -> "6"
            SpeakerPhraseToken.SEVEN -> "7"
            SpeakerPhraseToken.EIGHT -> "8"
            SpeakerPhraseToken.NINE -> "9"
            SpeakerPhraseToken.TEN -> "10"
            SpeakerPhraseToken.ELEVEN -> "11"
            SpeakerPhraseToken.TWELVE -> "12"
            SpeakerPhraseToken.THIRTEEN -> "13"
            SpeakerPhraseToken.FOURTEEN -> "14"
            SpeakerPhraseToken.FIFTEEN -> "15"
            SpeakerPhraseToken.SIXTEEN -> "16"
            SpeakerPhraseToken.SEVENTEEN -> "17"
            SpeakerPhraseToken.EIGHTEEN -> "18"
            SpeakerPhraseToken.NINETEEN -> "19"

            SpeakerPhraseToken.TWENTY -> "20"
            SpeakerPhraseToken.THIRTY -> "30"
            SpeakerPhraseToken.FORTY -> "40"
            SpeakerPhraseToken.FIFTY -> "50"
            SpeakerPhraseToken.SIXTY -> "60"
            SpeakerPhraseToken.SEVENTY -> "70"
            SpeakerPhraseToken.EIGHTY -> "80"
            SpeakerPhraseToken.NINETY -> "90"

            SpeakerPhraseToken.ONE_HUNDRED -> "100"
            SpeakerPhraseToken.TWO_HUNDRED -> "200"
            SpeakerPhraseToken.THREE_HUNDRED -> "300"
            SpeakerPhraseToken.FOUR_HUNDRED -> "400"
            SpeakerPhraseToken.FIVE_HUNDRED -> "500"
            SpeakerPhraseToken.SIX_HUNDRED -> "600"
            SpeakerPhraseToken.SEVEN_HUNDRED -> "700"
            SpeakerPhraseToken.EIGHT_HUNDRED -> "800"
            SpeakerPhraseToken.NINE_HUNDRED -> "900"
            SpeakerPhraseToken.ONE_HUNDRED_EXACTLY -> "100"

            SpeakerPhraseToken.FIRST -> "1st"
            SpeakerPhraseToken.SECOND -> "2nd"
            SpeakerPhraseToken.THIRD -> "3rd"
            SpeakerPhraseToken.FOURTH -> "4th"
            SpeakerPhraseToken.FIFTH -> "5th"
            SpeakerPhraseToken.SIXTH -> "6th"
            SpeakerPhraseToken.SEVENTH -> "7th"
            SpeakerPhraseToken.EIGHTH -> "8th"
            SpeakerPhraseToken.NINTH -> "9th"
            SpeakerPhraseToken.TENTH -> "10th"
            SpeakerPhraseToken.ELEVENTH -> "11th"
            SpeakerPhraseToken.TWELFTH -> "12th"

            SpeakerPhraseToken.TOLL_ROAD_AHEAD -> "TollRoadAhead"
            SpeakerPhraseToken.SCHOOL_AHEAD -> "SchoolAhead"

            SpeakerPhraseToken.OVERTAKING_DANGER -> "OvertakingDanger"
            SpeakerPhraseToken.PEDESTRIAN_DANGER -> "PedestriansAhead"
            SpeakerPhraseToken.CROSSROAD_DANGER -> "DangerCrossroadAhead"

            SpeakerPhraseToken.SPEED_BUMP_AHEAD -> "SpeedBumpAhead"
            SpeakerPhraseToken.SEVERAL_SPEED_BUMPS_AHEAD -> "SeveralSpeedBumpsAhead"
            SpeakerPhraseToken.RAILWAY_CROSSING_AHEAD -> "RailwayCrossingAhead"

            SpeakerPhraseToken.ATTENTION -> "Attention"
            SpeakerPhraseToken.PARKING_ROUTE_AVAILABLE -> "ParkingRouteAvailable"
            SpeakerPhraseToken.AND_ONE -> "AndOne"
            SpeakerPhraseToken.EXIT_TURN__FEM -> "ExitTurnFem"
            SpeakerPhraseToken.EXIT_TURN__MASC -> "ExitTurnMasc"
            SpeakerPhraseToken.GET_LEFT -> "GetLeft"
            SpeakerPhraseToken.GET_RIGHT -> "GetRight"
            SpeakerPhraseToken.ROUNDABOUT -> "Roundabout"
            SpeakerPhraseToken.LANES_LOCATIVE -> "LanesLocative"
            SpeakerPhraseToken.DO_EXIT -> "DoExit"
            SpeakerPhraseToken.TUNNEL -> "Tunnel"
            SpeakerPhraseToken.BRIDGE -> "Bridge"

            SpeakerPhraseToken.WALK_STRAIGHT -> "WalkStraight"
            SpeakerPhraseToken.PEDESTRIAN_ROUTE_FINISHED -> "PedestrianRouteFinished"
            SpeakerPhraseToken.PEDESTRIAN_WAYPOINT_PASSED -> "PedestrianWaypointPassed"
            SpeakerPhraseToken.CROSSWALK -> "Crosswalk"
            SpeakerPhraseToken.INTO_UNDERPASS -> "IntoUnderpass"
            SpeakerPhraseToken.OUT_OF_UNDERPASS -> "OutOfUnderpass"
            SpeakerPhraseToken.INTO_OVERPASS -> "IntoOverpass"
            SpeakerPhraseToken.OUT_OF_OVERPASS -> "OutOfOverpass"
            SpeakerPhraseToken.STAIRS_UP -> "StairsUp"
            SpeakerPhraseToken.STAIRS_DOWN -> "StairsDown"
            SpeakerPhraseToken.STAIRS -> "Stairs"
            SpeakerPhraseToken.DISMOUNT -> "Dismount"
            SpeakerPhraseToken.ONTO_PEDESTRIAN_ROAD -> "OntoPedestrianRoad"
            SpeakerPhraseToken.ONTO_BICYCLE_ROAD -> "OntoBicycleRoad"
            SpeakerPhraseToken.ONTO_AUTO_ROAD -> "OntoAutoRoad"
            SpeakerPhraseToken.GET_OFF_AT_THE_STOP -> "GetOffAtTheStop"
            SpeakerPhraseToken.YOUR_STOP_IS_COMING_SOON -> "YourStopIsComingSoon"
            SpeakerPhraseToken.TRAVEL_TO_THE_STOP -> "TravelToTheStop"
            SpeakerPhraseToken.TAKE_THE_BUS -> "TakeTheBus"
            SpeakerPhraseToken.TAKE_THE_MINIBUS -> "TakeTheMinibus"
            SpeakerPhraseToken.TAKE_THE_RAILWAY -> "TakeTheRailway"
            SpeakerPhraseToken.TAKE_THE_SUBURBAN -> "TakeTheSuburban"
            SpeakerPhraseToken.TAKE_THE_TRAM -> "TakeTheTram"
            SpeakerPhraseToken.TAKE_THE_TROLLEYBUS -> "TakeTheTrolleybus"
            SpeakerPhraseToken.TAKE_THE_UNDERGROUND -> "TakeTheUnderground"
            SpeakerPhraseToken.TAKE_THE_TRANSPORT -> "TakeTheTransport"
            SpeakerPhraseToken.TAKE_THE_WATER_TRANSPORT -> "TakeTheWaterTransport"

            SpeakerPhraseToken.AR_ONE_GENITIVE -> "ArOneGenitive"
            SpeakerPhraseToken.AR_TWO_GENITIVE -> "ArTwoGenitive"
            SpeakerPhraseToken.AR_THREE_GENITIVE -> "ArThreeGenitive"
            SpeakerPhraseToken.AR_FOUR_GENITIVE -> "ArFourGenitive"
            SpeakerPhraseToken.AR_FIVE_GENITIVE -> "ArFiveGenitive"
            SpeakerPhraseToken.AR_SIX_GENITIVE -> "ArSixGenitive"
            SpeakerPhraseToken.AR_SEVEN_GENITIVE -> "ArSevenGenitive"
            SpeakerPhraseToken.AR_EIGHT_GENITIVE -> "ArEightGenitive"
            SpeakerPhraseToken.AR_NINE_GENITIVE -> "ArNineGenitive"
            SpeakerPhraseToken.AR_TEN_GENITIVE -> "ArTenGenitive"
            SpeakerPhraseToken.AR_ELEVEN_GENITIVE -> "ArElevenGenitive"
            SpeakerPhraseToken.AR_TWELVE_GENITIVE -> "ArTwelveGenitive"
            SpeakerPhraseToken.AR_THIRTEEN_GENITIVE -> "ArThirteenGenitive"
            SpeakerPhraseToken.AR_FOURTEEN_GENITIVE -> "ArFourteenGenitive"
            SpeakerPhraseToken.AR_FIFTEEN_GENITIVE -> "ArFifteenGenitive"
            SpeakerPhraseToken.AR_SIXTEEN_GENITIVE -> "ArSixteenGenitive"
            SpeakerPhraseToken.AR_SEVENTEEN_GENITIVE -> "ArSeventeenGenitive"
            SpeakerPhraseToken.AR_EIGHTEEN_GENITIVE -> "ArEighteenGenitive"
            SpeakerPhraseToken.AR_NINETEEN_GENITIVE -> "ArNineteenGenitive"
            SpeakerPhraseToken.AR_TWENTY_GENITIVE -> "ArTwentyGenitive"
            SpeakerPhraseToken.AR_THIRTY_GENITIVE -> "ArThirtyGenitive"
            SpeakerPhraseToken.AR_FORTY_GENITIVE -> "ArFortyGenitive"
            SpeakerPhraseToken.AR_FIFTY_GENITIVE -> "ArFiftyGenitive"
            SpeakerPhraseToken.AR_SIXTY_GENITIVE -> "ArSixtyGenitive"
            SpeakerPhraseToken.AR_SEVENTY_GENITIVE -> "ArSeventyGenitive"
            SpeakerPhraseToken.AR_EIGHTY_GENITIVE -> "ArEightyGenitive"
            SpeakerPhraseToken.AR_NINETY_GENITIVE -> "ArNinetyGenitive"
            SpeakerPhraseToken.AR_ONE_HUNDRED_GENITIVE -> "ArOneHundredGenitive"
            SpeakerPhraseToken.AR_TWO_HUNDRED_GENITIVE -> "ArTwoHundredGenitive"
            SpeakerPhraseToken.AR_THREE_HUNDRED_GENITIVE -> "ArThreeHundredGenitive"
            SpeakerPhraseToken.AR_FOUR_HUNDRED_GENITIVE -> "ArFourHundredGenitive"
            SpeakerPhraseToken.AR_FIVE_HUNDRED_GENITIVE -> "ArFiveHundredGenitive"
            SpeakerPhraseToken.AR_SIX_HUNDRED_GENITIVE -> "ArSixHundredGenitive"
            SpeakerPhraseToken.AR_SEVEN_HUNDRED_GENITIVE -> "ArSevenHundredGenitive"
            SpeakerPhraseToken.AR_EIGHT_HUNDRED_GENITIVE -> "ArEightHundredGenitive"
            SpeakerPhraseToken.AR_NINE_HUNDRED_GENITIVE -> "ArNineHundredGenitive"

            SpeakerPhraseToken.AR_KILOMETER_SINGULAR_NOMINATIVE -> "ArKilometerSingularNominative"
            SpeakerPhraseToken.AR_KILOMETER_SINGULAR_GENITIVE -> "ArKilometerSingularGenitive"
            SpeakerPhraseToken.AR_KILOMETER_SINGULAR_ACCUSATIVE -> "ArKilometerSingularAccusative"
            SpeakerPhraseToken.AR_KILOMETER_DUAL_NOMINATIVE -> "ArKilometerDualNominative"
            SpeakerPhraseToken.AR_KILOMETER_DUAL_GENITIVE -> "ArKilometerDualGenitive"
            SpeakerPhraseToken.AR_KILOMETER_PLURAL_GENITIVE -> "ArKilometerPluralGenitive"
            SpeakerPhraseToken.AR_METER_SINGULAR_NOMINATIVE -> "ArMeterSingularNominative"
            SpeakerPhraseToken.AR_METER_SINGULAR_GENITIVE -> "ArMeterSingularGenitive"
            SpeakerPhraseToken.AR_METER_SINGULAR_ACCUSATIVE -> "ArMeterSingularAccusative"
            SpeakerPhraseToken.AR_METER_DUAL_NOMINATIVE -> "ArMeterDualNominative"
            SpeakerPhraseToken.AR_METER_DUAL_GENITIVE -> "ArMeterDualGenitive"
            SpeakerPhraseToken.AR_METER_PLURAL_GENITIVE -> "ArMeterPluralGenitive"

            // Lanes annotations
            SpeakerPhraseToken.TAKE_LEFT_LANE -> "TakeLeftLane"
            SpeakerPhraseToken.TAKE_MIDDLE_LANE -> "TakeMiddleLane"
            SpeakerPhraseToken.TAKE_RIGHT_LANE -> "TakeRightLane"
            SpeakerPhraseToken.TAKE_SECOND_LEFT_LANE -> "TakeSecondLeftLane"
            SpeakerPhraseToken.TAKE_SECOND_RIGHT_LANE -> "TakeSecondRightLane"
            SpeakerPhraseToken.TAKE_THIRD_LEFT_LANE -> "TakeThirdLeftLane"
            SpeakerPhraseToken.TAKE_THIRD_RIGHT_LANE -> "TakeThirdRightLane"
            SpeakerPhraseToken.TAKE_LEFT_LANES -> "TakeLeftLanes"
            SpeakerPhraseToken.TAKE_MIDDLE_LANES -> "TakeMiddleLanes"
            SpeakerPhraseToken.TAKE_RIGHT_LANES -> "TakeRightLanes"

            SpeakerPhraseToken.ES_TWENTY_ONE -> "EsTwentyOne"
            SpeakerPhraseToken.ES_TWENTY_TWO -> "EsTwentyTwo"
            SpeakerPhraseToken.ES_TWENTY_THREE -> "EsTwentyThree"
            SpeakerPhraseToken.ES_TWENTY_FOUR -> "EsTwentyFour"
            SpeakerPhraseToken.ES_TWENTY_FIVE -> "EsTwentyFive"
            SpeakerPhraseToken.ES_TWENTY_SIX -> "EsTwentySix"
            SpeakerPhraseToken.ES_TWENTY_SEVEN -> "EsTwentySeven"
            SpeakerPhraseToken.ES_TWENTY_EIGHT -> "EsTwentyEight"
            SpeakerPhraseToken.ES_TWENTY_NINE -> "EsTwentyNine"

            SpeakerPhraseToken.HY_TWENTY_TWO -> "HyTwentyTwo"
            SpeakerPhraseToken.HY_TWENTY_THREE -> "HyTwentyThree"
            SpeakerPhraseToken.HY_THIRTY_TWO -> "HyThirtyTwo"
            SpeakerPhraseToken.HY_THIRTY_THREE -> "HyThirtyThree"
            SpeakerPhraseToken.HY_FORTY_TWO -> "HyFortyTwo"
            SpeakerPhraseToken.HY_FORTY_THREE -> "HyFortyThree"
            SpeakerPhraseToken.HY_FIFTY_TWO -> "HyFiftyTwo"
            SpeakerPhraseToken.HY_FIFTY_THREE -> "HyFiftyThree"
            SpeakerPhraseToken.HY_SIXTY_TWO -> "HySixtyTwo"
            SpeakerPhraseToken.HY_SIXTY_THREE -> "HySixtyThree"
            SpeakerPhraseToken.HY_SEVENTY_TWO -> "HySeventyTwo"
            SpeakerPhraseToken.HY_SEVENTY_THREE -> "HySeventyThree"
            SpeakerPhraseToken.HY_EIGHTY_TWO -> "HyEightyTwo"
            SpeakerPhraseToken.HY_EIGHTY_THREE -> "HyEightyThree"
            SpeakerPhraseToken.HY_NINETY_TWO -> "HyNinetyTwo"
            SpeakerPhraseToken.HY_NINETY_THREE -> "HyNinetyThree"
        }
}