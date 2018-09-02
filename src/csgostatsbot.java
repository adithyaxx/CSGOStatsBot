import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;

import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultPhoto;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class csgostatsbot extends TelegramLongPollingBot 
{
	String textInput = "", steamID = "";
	JsonObject obj = null;
	
    @SuppressWarnings("deprecation")
	@Override
    public void onUpdateReceived(Update update) 
    {
        if (update.hasMessage() && update.getMessage().hasText()) 
        {
        	if (update.getMessage().getText().contains("/get") && !update.getMessage().getText().equals("/get@csgo_stats_bot") && !update.getMessage().getText().equals("/getbanner@csgo_stats_bot") && !update.getMessage().getText().equals("/get") && !update.getMessage().getText().equals("/getbanner"))
        	{
	        	textInput = update.getMessage().getText();
	        	long chat_id = update.getMessage().getChatId();
	        	
	        	steamID = getTextInput();
	        	
	        	if (getSuccessCode(steamID, "success").equals("1"))
	        		//converting customurl to steamid
	        		steamID = getSuccessCode(steamID, "steamid");
	        	
	        	String objString = "http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?appid=730&key=" + BotConfig.API_KEY + "&steamid=" + steamID;
	            
	        	switch (switchOption(objString))
	        	{
	        		case 1:
		        		SendPhoto photo = new SendPhoto()
			                    .setChatId(chat_id)
			                    .setPhoto("http://csgo-stats.com/" + steamID + "/graphic.png");
		        		
		        		try {
							sendPhoto(photo);
						} catch (TelegramApiException e) {
							e.printStackTrace();
						}
		        		break;
		        	case 2:
		        		SendMessage message = new SendMessage()
		                .setChatId(chat_id)
		                .setParseMode("markdown")
		                .setText(getStats());
		        
				        try {
				        	sendMessage(message);
				        } catch (TelegramApiException e) {
				            e.printStackTrace();
				        }
				        break;
				    default:
				    	SendMessage errMsg = new SendMessage()
	                    .setChatId(chat_id)
	                    .setText("Error, profile not found");
	            
			            try {
			            	sendMessage(errMsg);
			            } catch (TelegramApiException e) {
			                e.printStackTrace();
			            }
			            break;
	        	}
        	}
        	else
        	{
        		long chat_id = update.getMessage().getChatId();
        		
        		SendMessage message = new SendMessage()
	                    .setChatId(chat_id)
	                    .setParseMode("markdown")
	                    .setText("I can fetch CS:GO stats. You can control me by sending these commands:\n\n*Commands*\n/get - retrieve player's stats\n/getbanner - retrieve player's stats banner\n\n*Examples*\n_/get 76561198063387072\n/get FlyingPenguin\n/getbanner FlyingDinosaur_");
        		
        		try {
					sendMessage(message);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
        	}
        }
        
        else if (update.hasInlineQuery() && update.getInlineQuery().hasQuery()) 
        {
        	steamID = update.getInlineQuery().getQuery();
        	String chat_id = update.getInlineQuery().getId();
        	//ArrayList<InlineQueryResult> results = new ArrayList<InlineQueryResult>();
        	
        	if (getSuccessCode(steamID, "success").equals("1"))
        		//converting customurl to steamid
        		steamID = getSuccessCode(steamID, "steamid");
        	
        	String objString = "http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?appid=730&key=" + BotConfig.API_KEY + "&steamid=" + steamID;
        	
        	if (switchOption(objString) != 0)
        	{
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
        	}
        }
    }

    @Override
    public String getBotUsername() {
        // TODO
        return "csgo_stats_bot";
    }

    @Override
    public String getBotToken() {
        // TODO
        return BotConfig.BOT_TOKEN;
    }
    
    public boolean isGetBanner()
    {
    	if (textInput.contains("/getbanner"))
    		return true;
    	else
    		return false;
    }
    
    public String getTextInput()
    {
    	if (isGetBanner())
    		return textInput.substring(11);
    	else
    		return textInput.substring(5);
    }
    
    public String getSuccessCode(String str, String steamId)
    {
    	String objString = "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=" + BotConfig.API_KEY + "&vanityurl=" + str;
    	InputStream input = null;
		
		try {
			input = new URL(objString).openStream();
			InputStreamReader isr = new InputStreamReader(input);
			obj = Json.parse(isr).asObject();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

    	JsonObject response = obj.get("response").asObject();
    	
    	if (steamId.equals("success"))
    		return String.valueOf(response.get(steamId).asInt());
    	else
    		return response.get(steamId).asString();
    }
    
    public String getPersonaName(String str)
    {
    	String objString = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + BotConfig.API_KEY + "&steamids=" + str;
    	InputStream input = null;
		
		try {
			input = new URL(objString).openStream();
			InputStreamReader isr = new InputStreamReader(input);
			obj = Json.parse(isr).asObject();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

    	JsonObject response = obj.get("response").asObject();
    	JsonArray array = response.get("players").asArray();
    	JsonObject arrObj = array.get(0).asObject();
    	
    	return arrObj.get("personaname").asString();
    }
    
    public String getStats()
    {
    	Double totalTime = 0.0, totalDeaths = 0.0, totalHeadshots = 0.0, totalWins = 0.0, totalRounds = 0.0, totalShotsHit = 0.0, totalShotsFired = 0.0;
    	int totalKills = 0, totalMVPs = 0;
    	String messageText = "", personaName = "", kdRatio = "", totalTimeString = "", hsPercentage = "", accuracy = "", winPercentage = "";
    	int[] numArray = new int[31];
    	String[] wepArray = {("Glock-18"), 
			    			("Desert Eagle"), 
			    			("XM1014"), 
			    			("Dual Berettas"), 
			    			("Five-SeveN"), 
			    			("MAC-10"), 
			    			("UMP-45"), 
			    			("P90"), 
			    			("AWP"), 
			    			("AK-47"), 
			    			("AUG"), 
			    			("Famas"), 
			    			("G3SG1"), 
			    			("M249"), 
			    			("P2000"), 
			    			("P250"), 
			    			("SG 553"), 
			    			("SCAR-20"), 
			    			("SSG 08"),
			    			("MP7"),
			    			("MP9"),
			    			("Nova"),
			    			("Negev"),
			    			("Sawed-Off"),
			    			("PP-Bizon"),
			    			("Tec-9"),
			    			("Mag-7"),
			    			("M4"),
			    			("Galil AR"),
			    			("Zeus x27"),
			    			("Knife")};
    	
    	JsonObject player = obj.get("playerstats").asObject();
        JsonArray arr = player.get("stats").asArray();
        
        for (int i = 0; i < arr.size(); i++)
        {
        	JsonObject iObj = arr.get(i).asObject();
        	String name = iObj.get("name").asString();
        	Double num = iObj.get("value").asDouble();
        	
        	if (name.equals("total_kills"))
        		totalKills = num.intValue();
        	if (name.equals("total_deaths"))
        		totalDeaths = num;
            if (name.equals("total_time_played"))
            	totalTime = num;
            if (name.equals("total_kills_headshot"))
            	totalHeadshots = num;
            if (name.equals("total_wins"))
            	totalWins = num;
            if (name.equals("total_rounds_played"))
            	totalRounds = num;
            if (name.equals("total_shots_hit"))
            	totalShotsHit = num;
            if (name.equals("total_shots_fired"))
            	totalShotsFired = num;
            if (name.equals("total_mvps"))
            	totalMVPs = num.intValue();
            if (name.equals("total_kills_knife"))
            	numArray[30] = num.intValue();
            if (name.equals("total_kills_glock"))
            	numArray[0] = num.intValue();
            if (name.equals("total_kills_deagle"))
            	numArray[1] = num.intValue();
            if (name.equals("total_kills_elite"))
            	numArray[3] = num.intValue();
            if (name.equals("total_kills_fiveseven"))
            	numArray[4] = num.intValue();
            if (name.equals("total_kills_xm1014"))
            	numArray[2] = num.intValue();
            if (name.equals("total_kills_mac10"))
            	numArray[5] = num.intValue();
            if (name.equals("total_kills_ump45"))
            	numArray[6] = num.intValue();
            if (name.equals("total_kills_p90"))
            	numArray[7] = num.intValue();
            if (name.equals("total_kills_awp"))
            	numArray[8] = num.intValue();
            if (name.equals("total_kills_ak47"))
            	numArray[9] = num.intValue();
            if (name.equals("total_kills_aug"))
            	numArray[10] = num.intValue();
            if (name.equals("total_kills_famas"))
            	numArray[11] = num.intValue();
            if (name.equals("total_kills_g3sg1"))
            	numArray[12] = num.intValue();
            if (name.equals("total_kills_m249"))
            	numArray[13] = num.intValue();
            if (name.equals("total_kills_hkp2000"))
            	numArray[14] = num.intValue();
            if (name.equals("total_kills_p250"))
            	numArray[15] = num.intValue();
            if (name.equals("total_kills_sg556"))
            	numArray[16] = num.intValue();
            if (name.equals("total_kills_scar20"))
            	numArray[17] = num.intValue();
            if (name.equals("total_kills_ssg08"))
            	numArray[18] = num.intValue();
            if (name.equals("total_kills_mp7"))
            	numArray[19] = num.intValue();
            if (name.equals("total_kills_mp9"))
            	numArray[20] = num.intValue();
            if (name.equals("total_kills_nova"))
            	numArray[21] = num.intValue();
            if (name.equals("total_kills_negev"))
            	numArray[22] = num.intValue();
            if (name.equals("total_kills_sawedoff"))
            	numArray[23] = num.intValue();
            if (name.equals("total_kills_bizon"))
            	numArray[24] = num.intValue();
            if (name.equals("total_kills_tec9"))
            	numArray[25] = num.intValue();
            if (name.equals("total_kills_mag7"))
            	numArray[26] = num.intValue();
            if (name.equals("total_kills_m4a1"))
            	numArray[27] = num.intValue();
            if (name.equals("total_kills_galilar"))
            	numArray[28] = num.intValue();
            if (name.equals("total_kills_taser"))
            	numArray[29] = num.intValue();
        }
        
        int index = 0;
        int largest = Integer.MIN_VALUE;
        for ( int i = 0; i < numArray.length; i++ )
        {
            if ( numArray[i] > largest )
            {
                largest = numArray[i];
                index = i;
            }
        }
        
        DecimalFormat df = new DecimalFormat("0.##");
        
        kdRatio = df.format(totalKills / totalDeaths);
        totalTimeString = df.format(totalTime / 3600);
        winPercentage = df.format(totalWins / totalRounds * 100);
        accuracy = df.format(totalShotsHit / totalShotsFired * 100);
        hsPercentage = df.format(totalHeadshots / totalKills * 100);
        
        personaName = getPersonaName(steamID);
        
        messageText = "*" + personaName + "'s CS:GO Stats:*\n\n• KD Ratio: " + kdRatio + "\n• Kills: " + totalKills + "\n• Time Played: " + totalTimeString + "h\n• Wins: " + winPercentage + "%\n• Accuracy: " + accuracy + "%\n• Headshots: " + hsPercentage + "%\n• MVP: " + totalMVPs + "\n• Best Weapon: " + wepArray[index];
        
        return messageText;
    }
    
    public int switchOption(String objString)
    {
    	InputStream input = null;
		obj = null;
		
		try {
			input = new URL(objString).openStream();
			InputStreamReader isr = new InputStreamReader(input);
			obj = Json.parse(isr).asObject();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		if (obj == null)
		{
			return 0;
		}
		
		else if (isGetBanner())
		{
			return 1;
		}
		
		else
		{
			return 2;
		}
    }
}