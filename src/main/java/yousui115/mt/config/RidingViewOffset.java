package yousui115.mt.config;

import net.minecraft.util.math.Vec3d;

public class RidingViewOffset
{
    public String className;

    public Vec3d firstPerson;

    public Vec3d thirdPerson;

    /**
     * ■
     */
    public RidingViewOffset(String classNameIn, Vec3d firstIn, Vec3d thirdIn)
    {
        className = classNameIn;
        firstPerson = firstIn;
        thirdPerson = thirdIn;
    }
}
