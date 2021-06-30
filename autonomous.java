
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

/**
 * This 2020-2021 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine the position of the Ultimate Goal game elements.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@Autonomous(name = "Concept: TensorFlow Object Detection Webcam", group = "Concept")

public class TensorflowAutonomous extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";
    private DcMotor motorr;
    private Servo armservo;
    private DcMotor motorl;
     private DcMotor armpickup;
    
    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY =
            " [redacted]";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {
        motorr = hardwareMap.dcMotor.get("motorr");
      armservo = hardwareMap.servo.get("armservo");
      motorl = hardwareMap.dcMotor.get("motorl");
      armpickup = hardwareMap.dcMotor.get("armpickup");
        int target_zone = 0;
        
    
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(2.0, 16.0/9.0);
        }

        /** Wait for the game to begin */
        armservo.setPosition(1);
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) 
        {
            motorr.setDirection(DcMotorSimple.Direction.REVERSE);
            while (opModeIsActive()) 
            {
              opModeIsActive();
              if (tfod != null) 
              {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) 
                {
                  telemetry.addData("# Object Detected", updatedRecognitions.size());
                  if (updatedRecognitions.size() == 0 ) 
                  {
                    // empty list.  no objects recognized.
                    telemetry.addData("TFOD", "No items detected.");
                    telemetry.addData("Target Zone", "A");
                    target_zone = 0;
                  }
                  else
                  {
                      // step through the list of recognitions and display boundary info.
                      int i = 0;
                      for (Recognition recognition : updatedRecognitions)
                      {
                        telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                        telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                        recognition.getLeft(), recognition.getTop());
                        telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                        recognition.getRight(), recognition.getBottom());
                        // check label to see which target zone to go after.
                        if (recognition.getLabel().equals("Single"))
                        {
                          telemetry.addData("Target Zone", "B");
                          target_zone = 1;
                        } 
                        else if (recognition.getLabel().equals("Quad"))
                        {
                          telemetry.addData("Target Zone", "C");
                          target_zone = 2;
                        } 
                        else 
                        {
                          telemetry.addData("Target Zone", "UNKNOWN");
                          target_zone = 0;
                        }
                      }
                   }
                }
                
                telemetry.addData("target zone value is", target_zone);
                telemetry.update();
                
              } // if tfod != NULL
              
              if (target_zone == 0)
              {
                  // Target A
                  motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorl.setTargetPosition(-7000);
                  motorr.setTargetPosition(-7000);
                  motorl.setPower(0.6);
                  motorr.setPower(0.6);
                  motorl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  sleep(5000);
                  motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorr.setTargetPosition(4000);
                  motorr.setPower(-1);
                  motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorl.setTargetPosition(-4000);
                  motorl.setPower(1);
                  motorl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  sleep(800);
                  armpickup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  armpickup.setTargetPosition(400);
                  armpickup.setPower(-0.4);
                  armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  sleep(2000);
                  armservo.setPosition(0);
                  sleep(2000);
                  armpickup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  armpickup.setTargetPosition(-400);
                  armpickup.setPower(.4);
                  armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  sleep(2000);
                  
                  sleep(30000);
              }
              else if (target_zone ==1)
              {
                  // Target B
                  motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorl.setTargetPosition(-7500);
                  motorr.setTargetPosition(-7500);
                  motorl.setPower(0.6);
                  motorr.setPower(0.6);
                  motorl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  sleep(4000);
                  motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorr.setTargetPosition(-2000);
                  motorr.setPower(-1);
                  motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorl.setTargetPosition(2000);
                  motorl.setPower(1);
                  sleep(1000);
                  armpickup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  armpickup.setTargetPosition(400);
                  armpickup.setPower(-0.4);
                  armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  sleep(2000);
                  armservo.setPosition(0);
                  sleep(2000);
                  armpickup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  armpickup.setTargetPosition(-400);
                  armpickup.setPower(0.4);
                  armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  sleep(1500);
                  motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorl.setTargetPosition(1500);
                  motorr.setTargetPosition(1500);
                  motorl.setPower(0.6);
                  motorr.setPower(0.6);
                  motorl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  sleep(30000);
                
              }
              else
              {
                  // Target C
                  motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorl.setTargetPosition(-12000);
                  motorr.setTargetPosition(-12000);
                  motorl.setPower(0.6);
                  motorr.setPower(0.6);
                  motorl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  sleep(10000);
                  motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorr.setTargetPosition(5500);
                  motorr.setPower(-1);
                  motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorl.setTargetPosition(-5500);
                  motorl.setPower(1);
                  motorl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  sleep(2000);
                  motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorl.setTargetPosition(-1000);
                  motorr.setTargetPosition(-1000);
                  motorl.setPower(0.6);
                  motorr.setPower(0.6);
                  motorl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  sleep(2000);
                  armpickup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  armpickup.setTargetPosition(400);
                  armpickup.setPower(0.4);
                  armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  sleep(2000);
                  armservo.setPosition(0);
                  sleep(2000);
                  armpickup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  armpickup.setTargetPosition(-400);
                  armpickup.setPower(.4);
                  armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  sleep(2000);
                  motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                  motorl.setTargetPosition(6800);
                  motorr.setTargetPosition(6800);
                  motorl.setPower(0.6);
                  motorr.setPower(0.6);
                  motorl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                  
                  sleep(30000);
              }
            }  // while op mode is active
        } // if op mode is active

        if (tfod != null) {
            tfod.shutdown();
        }
        
    }

/*****
                      if (targetb == 2) {
            motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorl.setTargetPosition(-7500);
      motorr.setTargetPosition(-7500);
      motorl.setPower(0.6);
      motorr.setPower(0.6);
      motorl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      sleep(4000);
      motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorr.setTargetPosition(-2000);
      motorr.setPower(-1);
      motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorl.setTargetPosition(2000);
      motorl.setPower(1);
      sleep(1000);
      armpickup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      armpickup.setTargetPosition(400);
      armpickup.setPower(-0.4);
      armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      sleep(2000);
      armservo.setPosition(0);
      sleep(2000);
      armpickup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      armpickup.setTargetPosition(-400);
      armpickup.setPower(0.4);
      armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      sleep(1500);
      
      sleep(30000);
        }
      if (targetc == 3){
         motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorl.setTargetPosition(-12000);
      motorr.setTargetPosition(-12000);
      motorl.setPower(0.6);
      motorr.setPower(0.6);
      motorl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      sleep(10000);
      motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorr.setTargetPosition(5500);
      motorr.setPower(-1);
      motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorl.setTargetPosition(-5500);
      motorl.setPower(1);
      motorl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      sleep(2000);
      motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorl.setTargetPosition(-1000);
      motorr.setTargetPosition(-1000);
      motorl.setPower(0.6);
      motorr.setPower(0.6);
      motorl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      sleep(2000);
      armpickup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      armpickup.setTargetPosition(400);
      armpickup.setPower(0.4);
      armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      sleep(2000);
      armservo.setPosition(0);
      sleep(2000);
      armpickup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      armpickup.setTargetPosition(-400);
      armpickup.setPower(.4);
      armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      sleep(2000);
      motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorl.setTargetPosition(6800);
      motorr.setTargetPosition(6800);
      motorl.setPower(0.6);
      motorr.setPower(0.6);
      motorl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      
      sleep(30000);
      }
      if (targetb + targetc == 0){
        motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorl.setTargetPosition(-7000);
      motorr.setTargetPosition(-7000);
      motorl.setPower(0.6);
      motorr.setPower(0.6);
      motorl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      sleep(5000);
      motorr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorr.setTargetPosition(4000);
      motorr.setPower(-1);
      motorr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      motorl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      motorl.setTargetPosition(-4000);
      motorl.setPower(1);
      motorl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      sleep(800);
      armpickup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      armpickup.setTargetPosition(400);
      armpickup.setPower(-0.4);
      armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      sleep(2000);
      armservo.setPosition(0);
      sleep(2000);
      armpickup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      armpickup.setTargetPosition(-400);
      armpickup.setPower(.4);
      armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      sleep(2000);
      
      sleep(30000);
      }
*******/
    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
       tfodParameters.minResultConfidence = 0.8f;
       tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
       tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }
    

    
}
