import os
import time
import telepot
from telepot.loop import MessageLoop
import requests

API_KEY = "446588EC38D8160DE6825B649A9BA781"
TOKEN = "416084668:AAEkVYtRt8NmB_CdToFsR5ySV4lEb2NeXrE"
API_KEY = os.environ['API_KEY']
TOKEN = os.environ['TOKEN']

obj = dict()

def on_chat_message(msg):
    content_type, chat_type, chat_id = telepot.glance(msg)

    print(msg['text'])

    if msg['text'] in ["/get@csgo_stats_bot", "/getbanner@csgo_stats_bot", "/get", "/getbanner"] or "/get" not in msg['text'][0:4]:
        bot.sendMessage(chat_id,
                        "I can fetch CS:GO stats. You can control me by sending these commands:\n\n*Commands*\n/get - retrieve player's stats\n/getbanner - retrieve player's stats banner\n\n*Examples*\n_/get 76561198063387072\n/get FlyingPenguin\n/getbanner FlyingDinosaur_",
                        parse_mode="markdown")

    elif "/get" in msg['text'][0:4]:
        steamID = get_text_input(msg['text'])

        if get_success_code(steamID, "success") == "1":
            steamID = get_success_code(steamID, "steamid")
            print(steamID)

        objString = "http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?appid=730&key=" + API_KEY + "&steamid=" + steamID

        print(objString)

        try:
            obj2 = requests.get(objString).json()
        except ValueError:
            bot.sendMessage(chat_id, "The data could not be processed. Either an invalid command was entered or the user's profile is set to private.\n\nIf this is your profile, you can resolve it by going to your *Steam Profile --> Edit Profile --> Privacy Settings --> Set: \"My profile: Public\" and \"Game details: public\"*\n\nPlease note: Steam may take a while to process your privacy changes, so please try again in a couple of hours!", parse_mode="markdown")

        if obj2 is None:
            bot.sendMessage(chat_id, "Error, profile not found")

        elif is_get_banner(msg['text']):
            bot.sendPhoto(chat_id, "http://csgo-stats.com/" + steamID + "/graphic.png")

        else:
            bot.sendMessage(chat_id, get_stats(obj2, steamID), parse_mode="markdown")

    '''
    elif "inline_query" in flavor(msg['text']):
        steamID = msg['text']['query']

        if get_success_code(steamID, "success") == "1":
            steamID = get_success_code(steamID, "steamid")

        objString = "http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?appid=730&key=" + BotConfig.API_KEY + "&steamid=" + steamID

        if switch_option(objString) is not 0:

            InputTextMessageContent itmc = new InputTextMessageContent()
                    .setMessageText(getStats());

            InlineQueryResultPhoto iqrTxt = new InlineQueryResultPhoto()
                    .setId("1")
                    .setInputMessageContent(itmc)
                    .setPhotoUrl("https://i.imgur.com/ffnLtZJ.jpg")
                    .setThumbUrl("https://i.imgur.com/ffnLtZJ.jpg");

            InlineQueryResultPhoto iqrBnr = new InlineQueryResultPhoto()
                    .setId("2")
                    .setPhotoUrl("http://csgo-stats.com/" + steamID + "/graphic.png")
                    .setThumbUrl("https://i.imgur.com/4ViZwBn.jpg");

            AnswerInlineQuery aiq = new AnswerInlineQuery()
                    .setInlineQueryId(chat_id)
                    .setResults(iqrTxt, iqrBnr);

            try {
                answerInlineQuery(aiq);
            } catch (TelegramApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    '''

def on_callback_query(msg):
    query_id, from_id, query_data = telepot.glance(msg, flavor='callback_query')
    print('Callback Query:', query_id, from_id, query_data)

    bot.answerCallbackQuery(query_id, text='Got it')


def get_text_input(textInput):
    if is_get_banner(textInput):
        return textInput[11:]
    else:
        return textInput[5:]


def is_get_banner(textInput):
    if "/getbanner" in textInput:
        return 1
    else:
        return 0


def get_success_code(strng, steamId):
    objString = "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=" + API_KEY + "&vanityurl=" + strng
    global obj
    obj = requests.get(objString).json()
    response = obj["response"]

    if steamId is 'success':
        return str(int(response[steamId]))
    else:
        return str(response[steamId])


def get_persona_name(strng):
    objString = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + API_KEY + "&steamids=" + strng

    global obj
    obj = requests.get(objString).json()
    array = obj['response']['players']
    arrObj = array[0]

    return arrObj['personaname']


def get_stats(obj2, steamID):
    numArray = [None]*31
    totalTime = 0.0
    totalDeaths = 0.0
    totalHeadshots = 0.0
    totalWins = 0.0
    totalRounds = 0.0
    totalShotsHit = 0.0
    totalShotsFired = 0.0
    totalKills = 0
    totalMVPs = 0
    messageText = ""
    personaName = ""
    kdRatio = ""
    totalTimeString = ""
    hsPercentage = ""
    accuracy = ""
    winPercentage = ""
    wepArray = ["Glock-18",
                "Desert Eagle",
                "XM1014",
                "Dual Berettas",
                "Five-SeveN",
                "MAC-10",
                "UMP-45",
                "P90",
                "AWP",
                "AK-47",
                "AUG",
                "Famas",
                "G3SG1",
                "M249",
                "P2000",
                "P250",
                "SG 553",
                "SCAR-20",
                "SSG 08",
                "MP7",
                "MP9",
                "Nova",
                "Negev",
                "Sawed-Off",
                "PP-Bizon",
                "Tec-9",
                "Mag-7",
                "M4",
                "Galil AR",
                "Zeus x27",
                "Knife"]

    player = obj2['playerstats']
    arr = player["stats"]

    for i in range(0, len(arr)):
        iObj = arr[i]
        name = str(iObj["name"])
        num = str(iObj["value"])

        if name == "total_kills":
            totalKills = int(num)
        if name == "total_deaths":
            totalDeaths = int(num)
        if name == "total_time_played":
            totalTime = int(num)
        if name == "total_kills_headshot":
            totalHeadshots = int(num)
        if name == "total_wins":
            totalWins = int(num)
        if name == "total_rounds_played":
            totalRounds = int(num)
        if name == "total_shots_hit":
            totalShotsHit = int(num)
        if name == "total_shots_fired":
            totalShotsFired = int(num)
        if name == "total_mvps":
            totalMVPs = int(num)
        if name == "total_kills_knife":
            numArray[30] = int(num)
        if name == "total_kills_glock":
            numArray[0] = int(num)
        if name == "total_kills_deagle":
            numArray[1] = int(num)
        if name == "total_kills_elite":
            numArray[3] = int(num)
        if name == "total_kills_fiveseven":
            numArray[4] = int(num)
        if name == "total_kills_xm1014":
            numArray[2] = int(num)
        if name == "total_kills_mac10":
            numArray[5] = int(num)
        if name == "total_kills_ump45":
            numArray[6] = int(num)
        if name == "total_kills_p90":
            numArray[7] = int(num)
        if name == "total_kills_awp":
            numArray[8] = int(num)
        if name == "total_kills_ak47":
            numArray[9] = int(num)
        if name == "total_kills_aug":
            numArray[10] = int(num)
        if name == "total_kills_famas":
            numArray[11] = int(num)
        if name == "total_kills_g3sg1":
            numArray[12] = int(num)
        if name == "total_kills_m249":
            numArray[13] = int(num)
        if name == "total_kills_hkp2000":
            numArray[14] = int(num)
        if name == "total_kills_p250":
            numArray[15] = int(num)
        if name == "total_kills_sg556":
            numArray[16] = int(num)
        if name == "total_kills_scar20":
            numArray[17] = int(num)
        if name == "total_kills_ssg08":
            numArray[18] = int(num)
        if name == "total_kills_mp7":
            numArray[19] = int(num)
        if name == "total_kills_mp9":
            numArray[20] = int(num)
        if name == "total_kills_nova":
            numArray[21] = int(num)
        if name == "total_kills_negev":
            numArray[22] = int(num)
        if name == "total_kills_sawedoff":
            numArray[23] = int(num)
        if name == "total_kills_bizon":
            numArray[24] = int(num)
        if name == "total_kills_tec9":
            numArray[25] = int(num)
        if name == "total_kills_mag7":
            numArray[26] = int(num)
        if name == "total_kills_m4a1":
            numArray[27] = int(num)
        if name == "total_kills_galilar":
            numArray[28] = int(num)
        if name == "total_kills_taser":
            numArray[29] = int(num)

    index = numArray.index(max(numArray))

    kdRatio = '{0:.2f}'.format(totalKills / totalDeaths)
    totalTimeString = '{0:.2f}'.format(totalTime / 3600)
    winPercentage = '{0:.2f}'.format(totalWins / totalRounds * 100)
    accuracy = '{0:.2f}'.format(totalShotsHit / totalShotsFired * 100)
    hsPercentage = '{0:.2f}'.format(totalHeadshots / totalKills * 100)

    personaName = get_persona_name(steamID)

    messageText = "*" + str(personaName) + "'s CS:GO Stats:*\n\n• KD Ratio: " + str(kdRatio) + "\n• Kills: " + str(totalKills) + "\n• Time Played: " + str(totalTimeString) + "h\n• Wins: " + str(winPercentage) + "%\n• Accuracy: " + str(accuracy) + "%\n• Headshots: " + str(hsPercentage) + "%\n• MVP: " + str(totalMVPs) + "\n• Best Weapon: " + \
                  wepArray[index]

    return messageText


bot = telepot.Bot(TOKEN)
MessageLoop(bot, {'chat': on_chat_message,
                  'callback_query': on_callback_query}).run_as_thread()
print('Listening ...')

while 1:
    time.sleep(10)
