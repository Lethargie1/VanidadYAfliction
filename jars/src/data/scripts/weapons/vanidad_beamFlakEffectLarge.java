/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.scripts.weapons;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Fabrice Valade
 */
public class vanidad_beamFlakEffectLarge extends vanidad_beamFlakEffect {
    private final Color PARTICLE_COLOR = new Color(215, 225, 255, 255);
    private final float EXPLOSION_DAMAGE = 300f;
    private final float EXPLOSION_RANGE = 350f;
    private final float MIN_RANGE_FOR_FULL_EXPLOSION  = 800;
    private final float MIN_RANGE_FOR_NO_EXPLOSION  = 250;
    private BeamFlakExplosion flak = new BeamFlakExplosion(MIN_RANGE_FOR_FULL_EXPLOSION, MIN_RANGE_FOR_NO_EXPLOSION, EXPLOSION_DAMAGE, EXPLOSION_RANGE, 2f);
    
    @Override
    protected BeamFlakExplosion getFlakInstance() {
        return flak;
    }

    @Override
    protected void spawnFlakVisual(CombatEngineAPI engine, Vector2f end, float distanceRatio, BeamAPI beam) {
         Color beamCore = beam.getCoreColor();
        Color explosionColor = Misc.interpolateColor(beamCore, Color.white, 0.4f);
        Color riftColor = Misc.setAlpha(beamCore, 50);
        engine.spawnExplosion(end,
                              new Vector2f(0, 0),
                              explosionColor,
                              EXPLOSION_RANGE * distanceRatio,
                              0.5f);
        engine.addNegativeSwirlyNebulaParticle(end,
                                               new Vector2f(0, 0),
                                               EXPLOSION_RANGE * distanceRatio * 0.5f,
                                               2f,
                                               0.1f,
                                               0f,
                                               2f,
                                               Color.white);
        engine.addSwirlyNebulaParticle(end,
                                       new Vector2f(0, 0),
                                       EXPLOSION_RANGE * distanceRatio,
                                       2f,
                                       0.1f,
                                       0f,
                                       2f,
                                       riftColor,
                                       true);
        vanidad_negativeExplosionVisual.NEParams p = new vanidad_negativeExplosionVisual.NEParams();
        p.hitGlowSizeMult = .75f;
        p.spawnHitGlowAt = 0f;
        p.noiseMag = 1f;
        p.fadeIn = 0.1f;
        p.underglow = riftColor;
        p.withHitGlow = true;
        p.radius = 35f;
        p.color = explosionColor;
        Vector2f loc = new Vector2f(end);
        CombatEntityAPI e = engine.addLayeredRenderingPlugin(
                new vanidad_negativeExplosionVisual(p));
        e.getLocation().set(loc);
    }
    
}
