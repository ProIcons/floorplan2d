package floorplan2d.ExtendedInternalClasses;

/**
 *
 * @author Tasos
 */
public class Dimension2D  {
    public static class Double extends java.awt.geom.Dimension2D implements java.io.Serializable {

        public double width;
        public double height;

        public Double(double w, double h) {
            width = w;
            height = h;
        }

        @Override
        public double getWidth() {
            return width;
        }

        @Override
        public double getHeight() {
            return height;
        }

        @Override
        public void setSize(double width, double height) {
            this.width = width;
            this.height = height;
        }
    }

}
