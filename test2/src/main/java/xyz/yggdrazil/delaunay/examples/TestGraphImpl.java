package xyz.yggdrazil.delaunay.examples;

import xyz.yggdrazil.delaunay.voronoi.Center;
import xyz.yggdrazil.delaunay.voronoi.VoronoiGraph;
import xyz.yggdrazil.fortune.Voronoi;

import java.awt.*;
import java.util.Random;

/**
 * TestGraphImpl.java
 * <p>
 * Supplies information for Voronoi graphing logic:
 * <p>
 * 1) biome mapping information based on a Site's elevation and moisture
 * <p>
 * 2) color mapping information based on biome, and for bodies of water
 *
 * @author Connor
 */
public class TestGraphImpl extends VoronoiGraph {

    public enum ColorData {

        OCEAN(0x44447a), LAKE(0x336699), BEACH(0xa09077), SNOW(0xffffff),
        TUNDRA(0xbbbbaa), BARE(0x888888), SCORCHED(0x555555), TAIGA(0x99aa77),
        SHURBLAND(0x889977), TEMPERATE_DESERT(0xc9d29b),
        TEMPERATE_RAIN_FOREST(0x448855), TEMPERATE_DECIDUOUS_FOREST(0x679459),
        GRASSLAND(0x88aa55), SUBTROPICAL_DESERT(0xd2b98b), SHRUBLAND(0x889977),
        ICE(0x99ffff), MARSH(0x2f6666), TROPICAL_RAIN_FOREST(0x337755),
        TROPICAL_SEASONAL_FOREST(0x559944), COAST(0x33335a),
        LAKESHORE(0x225588), RIVER(0x225588);
        public Color color;

        ColorData(int color) {
            this.color = new Color(color);
        }
    }

    public TestGraphImpl(Voronoi v, int numLloydRelaxations, Random r) {
        super(v, numLloydRelaxations, r);
        setOCEAN(ColorData.OCEAN.color);
        setLAKE(ColorData.LAKE.color);
        setBEACH(ColorData.BEACH.color);
        setRIVER(ColorData.RIVER.color);
    }

    @Override
    protected Color getColor(Enum biome) {
        return ((ColorData) biome).color;
    }

    @Override
    protected Enum getBiome(Center p) {
        if (p.getOcean()) {
            return ColorData.OCEAN;
        } else if (p.getWater()) {
            if (p.getElevation() < 0.1) {
                return ColorData.MARSH;
            }
            if (p.getElevation() > 0.8) {
                return ColorData.ICE;
            }
            return ColorData.LAKE;
        } else if (p.getCoast()) {
            return ColorData.BEACH;
        } else if (p.getElevation() > 0.8) {
            if (p.getMoisture() > 0.50) {
                return ColorData.SNOW;
            } else if (p.getMoisture() > 0.33) {
                return ColorData.TUNDRA;
            } else if (p.getMoisture() > 0.16) {
                return ColorData.BARE;
            } else {
                return ColorData.SCORCHED;
            }
        } else if (p.getElevation() > 0.6) {
            if (p.getMoisture() > 0.66) {
                return ColorData.TAIGA;
            } else if (p.getMoisture() > 0.33) {
                return ColorData.SHRUBLAND;
            } else {
                return ColorData.TEMPERATE_DESERT;
            }
        } else if (p.getElevation() > 0.3) {
            if (p.getMoisture() > 0.83) {
                return ColorData.TEMPERATE_RAIN_FOREST;
            } else if (p.getMoisture() > 0.50) {
                return ColorData.TEMPERATE_DECIDUOUS_FOREST;
            } else if (p.getMoisture() > 0.16) {
                return ColorData.GRASSLAND;
            } else {
                return ColorData.TEMPERATE_DESERT;
            }
        } else {
            if (p.getMoisture() > 0.66) {
                return ColorData.TROPICAL_RAIN_FOREST;
            } else if (p.getMoisture() > 0.33) {
                return ColorData.TROPICAL_SEASONAL_FOREST;
            } else if (p.getMoisture() > 0.16) {
                return ColorData.GRASSLAND;
            } else {
                return ColorData.SUBTROPICAL_DESERT;
            }
        }
    }
}
