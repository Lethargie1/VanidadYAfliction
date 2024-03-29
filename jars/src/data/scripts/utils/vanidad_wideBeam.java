/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



package data.scripts.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicAnim;
import data.scripts.util.MagicRender;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.List;
import org.lazywizard.lazylib.CollisionUtils;
import static org.lazywizard.lazylib.CollisionUtils.getCollides;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Fabrice Valade
 */
public class vanidad_wideBeam {
    public BeamAPI coreBeam;
    private float width;
    private Vector2f LastIntersectWakeDirection;
    private Vector2f directionVector;
    private Vector2f perpendicularDirectionVectorAddtoLess;
    private Vector2f perpendicularAdd;
    private Vector2f perpendicularLess;
    private enum Corners //bottom is getFrom +- perpendicular
    {
        BOTTOMADD,
        BOTTOMLESS,
        TOPADD,
        TOPLESS;
        
        public static Corners getCorners(int i) {
            return Corners.values()[i];
        }
      
    }
    public vanidad_shape rectangle = new vanidad_shape();
    public int numberOfComb = 1;
    public boolean IsExisting = false;
    
    public vanidad_wideBeam(){
        
    }
    public vanidad_wideBeam(BeamAPI beam, float width, int numberOfComb)
    {
        this.numberOfComb = numberOfComb;
        this.width = width;
        this.coreBeam = beam;
        Vector2f source = coreBeam.getFrom();
        Vector2f end = coreBeam.getTo();
        directionVector = VectorUtils.getDirectionalVector(source, end);
        perpendicularDirectionVectorAddtoLess = MakeRotatedVector(directionVector, -90);
        perpendicularAdd = MakeRotatedScaledVector(directionVector, 90, this.width/2);
        perpendicularLess = MakeRotatedScaledVector(directionVector, -90, this.width/2);
        this.UpdateShape();
        IsExisting = true;
    }

    private Vector2f GetPointFromProjectionDistance(Corners corner, float distance){
        Vector2f correction = new Vector2f(perpendicularDirectionVectorAddtoLess);
        VectorUtils.resize(correction, distance);
        Vector2f intersection = new Vector2f();
        Vector2f.add(correction,
                     this.GetCornerValue(corner),
                     intersection);
        return intersection;

    }
    private Parallels GetCombThroughBeam() {
        Parallels result = new Parallels();
        float interCombFraction = 1f / (float)numberOfComb;
        Vector2f displacement = new Vector2f();
        Vector2f start= new Vector2f();
        Vector2f end= new Vector2f();
        for (int i=0; i<=numberOfComb; i++){
            float combDistance = interCombFraction*width*i;
            start = GetPointFromProjectionDistance(Corners.BOTTOMADD,
                                                            combDistance);
            end = GetPointFromProjectionDistance(Corners.TOPADD,
                                                            combDistance);
            result.addLine(start, end);
        }
        return result;
    }
    
    //feature of the widebeams we might want to get
    public Vector2f GetVectorAlongDirection(float length){
        return vanidad_util.GetPointFrom(new Vector2f(0,0), directionVector, length);
    }
    public Vector2f GetVectorOnBeam(float length){
        return vanidad_util.GetPointFrom(coreBeam.getFrom(), directionVector, length);
    }
    public float GetLength(){
        Vector2f along = Vector2f.sub(coreBeam.getTo(), coreBeam.getFrom(),
                                      null);
        return along.length();
    } 
    public float GetAngle(){
        return (float)Math.toDegrees(Math.atan2(directionVector.y, directionVector.x));
    }
    
    //more involved stuff implicating another entity
    public boolean IsCollisionCircleIntersecting(CombatEntityAPI entity)    {
        return rectangle.isCollides(entity.getLocation(), entity.getCollisionRadius());
    }
    public boolean IsEntityIntersecting(CombatEntityAPI entity){
        BoundsAPI bounds = entity.getExactBounds();

        // Entities that lack bounds will use the collision circle instead
        if (bounds == null)
        {
            return this.IsCollisionCircleIntersecting(entity);
        }
        bounds.update(entity.getLocation(), entity.getFacing());
        return rectangle.isCollides(bounds);
    }
    public List<Vector2f> getCombCollisionPoint(CombatEntityAPI entity)
    {

        List<Vector2f> collisionPoints = new ArrayList<Vector2f>();
        Parallels comb = this.GetCombThroughBeam();
        for (int i = 0; i < comb.count(); i++) {
                        Vector2f lineStart = new Vector2f(comb.xstart.get(i),comb.ystart.get(i));
                        Vector2f lineEnd = new Vector2f(comb.xend.get(i),comb.yend.get(i));
                        Vector2f ShieldCollisionPoint = vanidad_util.RayIntersectionWithShield(
                                entity, lineStart, lineEnd);
                        if (ShieldCollisionPoint != null){
                            collisionPoints.add(ShieldCollisionPoint);
                            continue;
                        }
                        Vector2f collisionPoint = CollisionUtils.getCollisionPoint(lineStart, lineEnd, entity);
                        if (collisionPoint!= null)
                            collisionPoints.add(collisionPoint);
                    }
        
        return collisionPoints;
    }
    
    
    
    
    private static Vector2f MakeRotatedVector(Vector2f Source, float angle){
        Vector2f dest = new Vector2f(Source);
        VectorUtils.rotate(dest, angle);
        return dest;
    }
    private static Vector2f MakeRotatedScaledVector(Vector2f source, float angle, float finalLength)
    {
        Vector2f dest = new Vector2f();
        source.normalise(dest);
        VectorUtils.rotate(dest, angle);
        VectorUtils.resize(dest, finalLength);
        return dest;
    }
    
    private Vector2f GetCornerValue(Corners corner)
    {
        Vector2f source = new Vector2f();
        Vector2f toSum = new Vector2f();
        switch (corner)
        {
            case BOTTOMADD:
                toSum = perpendicularAdd;
                source = coreBeam.getFrom();
                break;
            case BOTTOMLESS:
                toSum = perpendicularLess;
                source = coreBeam.getFrom();
                break;
            case TOPADD:
                toSum = perpendicularAdd;
                source = coreBeam.getTo();
                break;
            case TOPLESS:
                toSum = perpendicularLess;
                source = coreBeam.getTo();
                break;
        }
        Vector2f result = new Vector2f();
        Vector2f.add(source, toSum, result);
        return result;
    }
    
    private void UpdateShape()
    {
        rectangle.clear();
        rectangle.addSegment(this.GetCornerValue(Corners.BOTTOMADD), this.GetCornerValue(Corners.BOTTOMLESS));
        rectangle.addSegment(this.GetCornerValue(Corners.TOPLESS));
        rectangle.addSegment(this.GetCornerValue(Corners.TOPADD));
    }
    
    
    
    public class Parallels{
        public List<Float> xstart = new ArrayList<Float>();
        public List<Float> ystart= new ArrayList<Float>();
        public List<Float> xend= new ArrayList<Float>();
        public List<Float> yend= new ArrayList<Float>();
        public int count(){
            return xstart.size();
        }
        public Parallels(){
            
        }
        public void addLine(Vector2f start, Vector2f end){
            xstart.add(start.x);
            ystart.add(start.y);
            xend.add(end.x);
            yend.add(end.y);
        }
        public void clear(){
            xstart.clear();
            ystart.clear();
            xend.clear();
            yend.clear();
        }
    }
}
