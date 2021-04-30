/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.loading.MuzzleFlashSpec;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Fabrice Valade
 */
public class vanidad_alternatingBeamLarge implements EveryFrameWeaponEffectPlugin {
    
     private final       Color LowerColor = new Color(251,102,41);
     private final       Color CenterColor = new Color(250,65,41);
     private final       Color UpperColor = new Color(255,30,0);
    private final float inacuracyAngle = 15;
    //----------------This area is for setting all offsets for the barrels: note that the turret and hardpoint version of the weapon *must* have an equal amount of offsets--------------------
    //Offsets for medium weapons
    private static Map<Integer, Vector2f> LargeHardpointOffsets = new HashMap<Integer, Vector2f>();
    static {
        LargeHardpointOffsets.put(0, new Vector2f(42f, 6.5f));
        LargeHardpointOffsets.put(1, new Vector2f(42f, -6.5f));
    }
    private static Map<Integer, Vector2f> LargeTurretOffsets = new HashMap<Integer, Vector2f>();
    static {
        LargeTurretOffsets.put(0, new Vector2f(26f, 6.5f));
        LargeTurretOffsets.put(1, new Vector2f(26f, -6.5f));

    }
    
    //-----------------------------------------------------------------------------END OF OFFSET SPECIFICATIONS---------------------------------------------------------------------------------

    //Instantiates variables we will use later
    private int counter = 0;
    private boolean runOnce = true;

    private Map<Integer, BeamAPI> beamMap = new HashMap<Integer, BeamAPI>();
    
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        //Don't run if we are paused, or our if weapon is null
        if (engine.isPaused() || weapon == null) {
            return;
        }

        //Resets the beam map and variables if we are not firing
        if (weapon.getChargeLevel() <= 0) {
            beamMap.clear();
            runOnce = true;
            return;
        }

        //If we are firing, start the code and change variables
        if (weapon.getChargeLevel() > 0f && runOnce) {
            runOnce = false;
            int counterForBeams = 0;
            for (BeamAPI beam : engine.getBeams()) {
                if (beam.getWeapon() == weapon) {
                    if (!beamMap.containsValue(beam)) {
                        beamMap.put(counterForBeams, beam);
                        counterForBeams++;
                    }
                }
            }
        } else {
            return;
        }

        //For converge code: hide the first beam by making it invisible, and ensure all further operations are done on the second beam
        int numOffset = 0;
        if (beamMap.get(1) != null) {
            beamMap.get(0).setCoreColor(new Color(0f, 0f, 0f));
            beamMap.get(0).setFringeColor(new Color(0f, 0f, 0f));
            numOffset = 1;
        }

        //The big if-block where the magic happens: change a weapon's fireOffset via the alternating pattern specified by small/medium/large Turret/Hardpoint Offsets
        if (weapon.getSize() == WeaponAPI.WeaponSize.LARGE) {
            counter++;
            if (!LargeHardpointOffsets.containsKey(counter)) {
                counter = 0;
            }
            weapon.ensureClonedSpec();
            /*List<WeaponSpecAPI> a = Global.getSettings().getAllWeaponSpecs();
            ListIterator<WeaponSpecAPI> aitr = a.listIterator();
            WeaponSpecAPI muzzleSource2 = null;
            while(aitr.hasNext()){    
                WeaponSpecAPI temp = aitr.next();
                String id = temp.getWeaponId();
                boolean test = id.equals("vanidad_estarayo_medium_flash");

                if (test){
                    muzzleSource2 = temp;
                    break;
                }
            }*/
            float randomInterpolate = 0.8f*(float)Math.random()-0.4f;
            
            Color finalColor;
            if (randomInterpolate>=0)
                finalColor = Misc.interpolateColor(CenterColor,
                        UpperColor,
                        randomInterpolate);
            else
                finalColor = Misc.interpolateColor(CenterColor,
                        LowerColor,
                        randomInterpolate*-1f);
            int green = finalColor.getGreen();
            int red = finalColor.getRed();
            int blue = finalColor.getBlue();
            Color fringe = new Color(red/2, green/2, blue/2);
            beamMap.get(0).setCoreColor(finalColor);
            beamMap.get(0).setFringeColor(fringe);
            
            float currentAngle = (inacuracyAngle*(float)Math.random())-inacuracyAngle/2;
            //float weapAngle = weapon.getCurrAngle();
            /*weapon.ensureClonedSpec();
            WeaponSlotAPI b = weapon.getSlot();
            boolean tester = b.isTurret();
            
            muzzleSource2.getTurretAngleOffsets().set(numOffset, currentAngle);
            engine.spawnMuzzleFlashOrSmoke(weapon.getShip(),
                    b,
                    muzzleSource2,
                    counter,
                    weapAngle+currentAngle);
            */        
            weapon.getSpec().getHardpointFireOffsets().set(numOffset, LargeHardpointOffsets.get(counter));
            weapon.getSpec().getHiddenFireOffsets().set(numOffset, LargeTurretOffsets.get(counter));
            weapon.getSpec().getTurretFireOffsets().set(numOffset, LargeTurretOffsets.get(counter));
            
            weapon.getSpec().getHardpointAngleOffsets().set(numOffset,currentAngle);
            weapon.getSpec().getTurretAngleOffsets().set(numOffset, currentAngle);

            
                
            
            
        } 
    }
}