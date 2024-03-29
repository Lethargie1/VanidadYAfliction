package data.missions.vaotest;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {

	public void defineMission(MissionDefinitionAPI api) {

		// Set up the fleets so we can add ships and fighter wings to them.
		// In this scenario, the fleets are attacking each other, but
		// in other scenarios, a fleet may be defending or trying to escape
		api.initFleet(FleetSide.PLAYER, "VAO", FleetGoal.ATTACK, false);
		api.initFleet(FleetSide.ENEMY, "ISS", FleetGoal.ATTACK, true);

//		api.getDefaultCommander(FleetSide.PLAYER).getStats().setSkillLevel(Skills.COORDINATED_MANEUVERS, 3);
//		api.getDefaultCommander(FleetSide.PLAYER).getStats().setSkillLevel(Skills.ELECTRONIC_WARFARE, 3);
		
		// Set a small blurb for each fleet that shows up on the mission detail and
		// mission results screens to identify each side.
		api.setFleetTagline(FleetSide.PLAYER, "'The might of the Vanidad y Afliction operation'");
		api.setFleetTagline(FleetSide.ENEMY, "Some lousy rustbucket");
		
		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("Test all these new ship");
		api.addBriefingItem("Notice the imbalance");
		api.addBriefingItem("Obtain useful insight");
		
		// Set up the player's fleet.  Variant names come from the
		// files in data/variants and data/variants/fighters
		//api.addToFleet(FleetSide.PLAYER, "station_small_Standard", FleetMemberType.SHIP, "Test Station", false);
		api.addToFleet(FleetSide.PLAYER, "vanidad_ionizador_support", FleetMemberType.SHIP, true);
                api.addToFleet(FleetSide.PLAYER, "vanidad_cazador_hunter", FleetMemberType.SHIP, true);
                api.addToFleet(FleetSide.PLAYER, "vanidad_halcon_leader", FleetMemberType.SHIP, true);
                api.addToFleet(FleetSide.PLAYER, "vanidad_forrajeador_explorer", FleetMemberType.SHIP, true);
                api.addToFleet(FleetSide.PLAYER, "vanidad_almadena_support", FleetMemberType.SHIP, true);
				api.addToFleet(FleetSide.PLAYER, "vanidad_pazjoya_guardian", FleetMemberType.SHIP, true);
				api.addToFleet(FleetSide.PLAYER, "vanidad_invasor_standard", FleetMemberType.SHIP, true);
				api.addToFleet(FleetSide.PLAYER, "vanidad_comandante_offensive", FleetMemberType.SHIP, true);
				api.addToFleet(FleetSide.PLAYER, "vanidad_cargador_elite", FleetMemberType.SHIP, true);
                                api.addToFleet(FleetSide.PLAYER, "vanidad_dominator_anchor", FleetMemberType.SHIP, true);
                                api.addToFleet(FleetSide.PLAYER, "vanidad_enforcer_escort", FleetMemberType.SHIP, true);
				

		
		// Set up the enemy fleet.
		api.addToFleet(FleetSide.ENEMY, "mule_d_pirates_Smuggler", FleetMemberType.SHIP,  false);
		api.addToFleet(FleetSide.ENEMY, "dominator_Support", FleetMemberType.SHIP, false);
                api.addToFleet(FleetSide.ENEMY, "lasher_Standard", FleetMemberType.SHIP, false);
		
		// Set up the map.
		float width = 12000f;
		float height = 12000f;
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		float minX = -width/2;
		float minY = -height/2;
		
		// Add an asteroid field
		api.addAsteroidField(minX, minY + height / 2, 0, 8000f,
							 20f, 70f, 100);
		
		api.addPlanet(0, 0, 50f, StarTypes.BLUE_SUPERGIANT, 250f, true);
		
	}

}
