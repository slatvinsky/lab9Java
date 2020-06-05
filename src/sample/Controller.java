package sample;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Controller implements Initializable {

    private double startX, endX, stepX;
    private double maxY, minY;
    private Point[] points;
    StringBuilder infoTextBuild;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Canvas drawGraph;

    @FXML
    private TextField InputX;

    @FXML
    private TextField EndX;

    @FXML
    private TextField InputStep;

    @FXML
    private Button buttonGenerate;

    @FXML
    private Text ErrorText;

    @FXML
    private Text InfoText;

    @FXML @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert drawGraph != null : "fx:id=\"drawGraph\" was not injected: check your FXML file 'sample.fxml'.";
        assert InputX != null : "fx:id=\"InputX\" was not injected: check your FXML file 'sample.fxml'.";
        assert EndX != null : "fx:id=\"EndX\" was not injected: check your FXML file 'sample.fxml'.";
        assert InputStep != null : "fx:id=\"InputStep\" was not injected: check your FXML file 'sample.fxml'.";
        assert buttonGenerate != null : "fx:id=\"buttonGenerate\" was not injected: check your FXML file 'sample.fxml'.";
        assert ErrorText != null : "fx:id=\"ErrorText\" was not injected: check your FXML file 'sample.fxml'.";
        assert InfoText != null : "fx:id=\"InfoText\" was not injected: check your FXML file 'sample.fxml'.";
        buttonGenerate.setOnMouseClicked(event -> {
            ErrorText.setVisible(false);
            regenerateGraph();
        });
        InputX.setOnKeyReleased(event -> {
            ErrorText.setVisible(false);
            if (InputX.getText().length() > 0) {
                startX = Double.parseDouble(InputX.getText());
                System.out.println(InputX.getText());
            }
        });
        EndX.setOnKeyReleased(event -> {
            ErrorText.setVisible(false);
            if (EndX.getText().length() > 0) {
                endX = Double.parseDouble(EndX.getText());
                System.out.println(EndX.getText());
            }
        });
        InputStep.setOnKeyReleased(event -> {
            ErrorText.setVisible(false);
            if (InputStep.getText().length() > 0) {
                stepX = Double.parseDouble(InputStep.getText());
                System.out.println(InputStep.getText());
            }
        });
    }

    private void regenerateGraph() {
        if (stepX <= 0) {ErrorText.setText("Error! Step too small!"); ErrorText.setVisible(true); return;}
        if (startX >= endX) {ErrorText.setText("Error! Invalid range!"); ErrorText.setVisible(true); return;}
        if (startX <= 0) {ErrorText.setText("Error! Start value less than or equals zero!"); ErrorText.setVisible(true); return;}
        if (endX-startX < stepX) {ErrorText.setText("Error! Step is higher than graph range!"); ErrorText.setVisible(true); return;}
        //System.out.println(startX+" "+endX+" : "+stepX);
        run();
        draw();
    }

    private void draw() {
        double dx = drawGraph.getWidth(), dy = drawGraph.getHeight(), xMultiplier = dx/(endX-startX), dyMult = maxY+Math.abs(minY), dyDiff = maxY-minY, yMultiplier = -dyDiff*dy/dyMult/8, xOff = xMultiplier*startX, yOff = dy/2;//dy/yMultiplier*dyDiff;
        GraphicsContext gc = drawGraph.getGraphicsContext2D();
        gc.clearRect(0,0,dx,dy);
        gc.setLineWidth(1);
        gc.setStroke(new Color(0.8,0,0,1));
        gc.strokeLine(0,dy/2,dx,dy/2);
        double x_ = 0, y_ = dy/40, aX = startX*xMultiplier;
        for (int i = 0; i < 11; i++) {
            gc.strokeLine(x_, dy/2+y_, x_, dy/2-y_);
            gc.strokeText(String.valueOf(round((x_+aX)/xMultiplier, 3)), x_, dy/2+dy/24);
            x_+=dx/10;
        }
        x_ = dx/48; y_ = dy; aX = dy/2;
        gc.setStroke(new Color(0,0.8,0,1));
        for (int i = 0; i < 9; i++) {
            gc.strokeLine(dx/2+x_, y_, dx/2-x_, y_);
            gc.strokeText(String.valueOf(round((y_-aX)/yMultiplier, 3)), dx/2+dx/40, y_-dy/24);
            y_-=dy/8;
        }
        gc.strokeLine(dx/2,0,dx/2,dy);
        gc.setStroke(new Color(1,1,1,1));
        gc.strokeLine(0,dy/2,points[0].x*xMultiplier-xOff,points[0].y*yMultiplier-yOff);
        for (int i = 0; i < points.length-1; i++) {
            gc.strokeLine(points[i].x*xMultiplier-xOff,points[i].y*yMultiplier+yOff,points[i+1].x*xMultiplier-xOff,points[i+1].y*yMultiplier+yOff);
        }
    }

    public void run() {
        infoTextBuild = new StringBuilder();
        double x = startX;
        int n = calcN(stepX);
        infoTextBuild.append("Steps: ").append(n);
        points = createArr(n);
        for (int i = 0; i < n; i++) {
            points[i].x = x;
            points[i].y = calcY(x,2.2);
            x+=stepX;
            //System.out.println(x);
        }
        int maxE_x = getMinMaxNum(points, true, false), minE_x = getMinMaxNum(points, false, false), maxE_y = getMinMaxNum(points, true, true), minE_y = getMinMaxNum(points, false, true);
        double[] xN = getSumAndArif(points, false), yN = getSumAndArif(points, true);
        infoTextBuild.append(" Xmin = ").append(round(points[minE_x].x, 3)).append(" Xmax = ").append(round(points[maxE_x].x,3)).append(" Ymin = ").append(round(points[minE_y].y,3)).append(" Ymax = ").append(round(points[maxE_y].y,3));
        infoTextBuild.append("\nXsum = ").append(round(xN[0],3)).append(" Xarif = ").append(round(xN[1],3)).append(" Ysum = ").append(round(yN[0],3)).append(" Yarif = ").append(round(yN[1], 3));
        maxY = points[maxE_y].y; minY = points[minE_y].y;
        InfoText.setText(infoTextBuild.toString());
    }

    private int getMinMaxNum(Point[] arr, boolean max, boolean isYArr) {
        double buff = 0;
        if (isYArr) {
            buff = arr[0].y;
        } else {
            buff = arr[0].x;
        }
        int n = 0;
        for (int i = 1; i < arr.length; i++) {
            if (isYArr) {
                if (arr[i].y < buff && !max) {
                    buff = arr[i].y;
                    n = i;
                } else if (buff < arr[i].y && max) {
                    buff = arr[i].y;
                    n = i;
                }
            } else {
                if (arr[i].x < buff && !max) {
                    buff = arr[i].x;
                    n = i;
                } else if (buff < arr[i].x && max) {
                    buff = arr[i].x;
                    n = i;
                }
            }
        }
        return n;
    }

    protected double[] getSumAndArif(Point[] arr, boolean isYArr) {
        double[] out = new double[2];
        double buff = 0;
        for (int i = 0; i < arr.length; i++) {
            if (isYArr) {
                buff += arr[i].y;
            } else {
                buff += arr[i].x;
            }
        }
        out[0] = buff;
        out[1] = buff/arr.length;
        return out;
    }

    protected Point[] createArr(int amountOfElem) {
        Point[] points = new Point[amountOfElem];
        for (int i = 0; i < amountOfElem; i++) {
            points[i] = new Point(0,0);
        }
        return points;
    }

    protected int calcN(double step) {
        int amount = 0;
        for (double i = startX; i <= endX; i+=step) {
            amount++;
        }
        return amount;
    }

    protected double calcY(double x, double t) {
        if (x <= 0.9) {
            return (Math.pow(Math.log10(x),3) + x*x)/Math.sqrt(x+t);
        } else if (x > 0.9) {
            return Math.cos(x)+t*Math.sin(x)*Math.sin(x);
        } else {
            return 0;
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

