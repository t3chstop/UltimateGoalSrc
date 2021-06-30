package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.Servo;
@TeleOp(name = "test5 (Blocks to Java)", group = "")
public class test5 extends LinearOpMode {

  private DcMotor motorr;
  private DcMotor motorl;
  private DcMotor flywheel1;
  private DcMotor flywheel2;
  private DcMotor conveyor;
  private DcMotor armpickup;
  private DcMotor intake;
  private Servo armservo;
  /**
   * This function is executed when this Op Mode is selected from the Driver Station.
   */
  @Override
  public void runOpMode() {
    int last_encoder_value1 = 0;
    int last_encoder_value2 = 0;
    int encoder_delta = 0;
    double last_millisecs = 0, millisecs_delta = 0;
    double ticks_per_sec1 = 0;
    double ticks_per_sec2 = 0;
    double correction_value = 0;
    double power1 = 0.44;
    double power2 = 0.43;
    float armconstantdown = 0;
    float armconstantup = 0;
    int armcorrection1 = 0;
    int armcorrection2 = 0;
    ElapsedTime ElapsedTime2 = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    

    motorr = hardwareMap.dcMotor.get("motorr");
    motorl = hardwareMap.dcMotor.get("motorl");
    flywheel1 = hardwareMap.dcMotor.get("flywheel1");
    flywheel2 = hardwareMap.dcMotor.get("flywheel2");
    conveyor = hardwareMap.dcMotor.get("conveyor");
    armpickup = hardwareMap.dcMotor.get("armpickup");
    intake = hardwareMap.dcMotor.get("intake");
    armservo = hardwareMap.servo.get("armservo");
    // Reverse one of the drive motors.
    // You will have to determine which motor to reverse for your robot.
    // In this example, the right motor was reversed so that positive
    // applied power makes it move the robot in the forward direction.
    motorr.setDirection(DcMotorSimple.Direction.REVERSE);
    waitForStart();
    if (opModeIsActive()) {
      // Put run blocks here.
      
      while (opModeIsActive()) {
        resetStartTime();
        armconstantup = (20 * gamepad1.right_trigger);
        armconstantdown = (-20 * gamepad1.left_trigger);
        
        armcorrection1 = (int)(armconstantup);
        armcorrection2 =  (int)armconstantdown;
        
        
        // Put loop blocks here.
        // Use left stick to drive and right stick to turn
        motorl.setPower(-gamepad1.left_stick_y + gamepad1.right_stick_x);
        motorr.setPower(-gamepad1.left_stick_y - gamepad1.right_stick_x);
        
        
        
        if (gamepad1.right_bumper) {
           millisecs_delta = ElapsedTime2.milliseconds() - last_millisecs;
           if (millisecs_delta >= 80)
           {
               last_millisecs = ElapsedTime2.milliseconds();
         
               encoder_delta = flywheel1.getCurrentPosition() - last_encoder_value1;
               last_encoder_value1 = encoder_delta + last_encoder_value1;
               ticks_per_sec1 = (encoder_delta/millisecs_delta) * 1000;
               correction_value = (970 - ticks_per_sec1)/30000;
               if (correction_value > 0.005)correction_value = 0.005;
               if (correction_value < -0.005) correction_value = -0.005;
               power1 = power1 + correction_value;
               if ( power1 > 0.48) power1 = 0.48;
               if (power1 < 0.35) power1 = 0.35;
               
               encoder_delta = flywheel2.getCurrentPosition() - last_encoder_value2;
               last_encoder_value2 = encoder_delta + last_encoder_value2;
               ticks_per_sec2 =  -1 * (encoder_delta/millisecs_delta) * 1000;
               correction_value = (970 - ticks_per_sec2)/30000;
               if (correction_value > 0.005)correction_value = 0.005;
               if (correction_value < -0.005) correction_value = -0.005;
               power2 = power2 + correction_value;
               if ( power2 > 0.48) power2 = 0.48;
               if (power2 < 0.35) power2 = 0.35;
               
               telemetry.addData("ticks 1 per second", ticks_per_sec1);
               telemetry.addData("ticks 2 per second", ticks_per_sec2);
               telemetry.addData("correction_value", correction_value);
               telemetry.addData("power1", power1);
               telemetry.addData("power2", power2);
               telemetry.update();
           }
          flywheel1.setPower(power1);
          flywheel2.setPower((-1.0) * power2);
        } else {
          flywheel1.setPower(0);
          flywheel2.setPower(0);
        }
        
        if (gamepad1.back) {
           millisecs_delta = ElapsedTime2.milliseconds() - last_millisecs;
           if (millisecs_delta >= 80)
           {
               last_millisecs = ElapsedTime2.milliseconds();
         
               encoder_delta = flywheel1.getCurrentPosition() - last_encoder_value1;
               last_encoder_value1 = encoder_delta + last_encoder_value1;
               ticks_per_sec1 = (encoder_delta/millisecs_delta) * 1000;
               correction_value = (880 - ticks_per_sec1)/30000;
               if (correction_value > 0.005)correction_value = 0.005;
               if (correction_value < -0.005) correction_value = -0.005;
               power1 = power1 + correction_value;
               if ( power1 > 0.45) power1 = 0.45;
               if (power1 < 0.25) power1 = 0.25;
               
               encoder_delta = flywheel2.getCurrentPosition() - last_encoder_value2;
               last_encoder_value2 = encoder_delta + last_encoder_value2;
               ticks_per_sec2 =  -1 * (encoder_delta/millisecs_delta) * 1000;
               correction_value = (880 - ticks_per_sec2)/30000;
               if (correction_value > 0.005)correction_value = 0.005;
               if (correction_value < -0.005) correction_value = -0.005;
               power2 = power2 + correction_value;
               if ( power2 > .45) power2 = 0.45;
               if (power2 < 0.25) power2 = 0.25;
               
               telemetry.addData("ticks 1 per second", ticks_per_sec1);
               telemetry.addData("ticks 2 per second", ticks_per_sec2);
               telemetry.addData("correction_value", correction_value);
               telemetry.addData("power1", power1);
               telemetry.addData("power2", power2);
               telemetry.update();
           }
          flywheel1.setPower(power1);
          flywheel2.setPower((-1.0) * power2);
        } else {
          flywheel1.setPower(0);
          flywheel2.setPower(0);
        }
        
        
        
        
        if (gamepad1.left_bumper) {
          conveyor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
          conveyor.setTargetPosition(9000);
          conveyor.setPower(1);
          conveyor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
        if (gamepad1.dpad_down) {
          
          
          armpickup.setTargetPosition(441 + armcorrection1 + armcorrection2);
          armpickup.setPower(0.4);
          armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        } 
        if (gamepad1.dpad_up) {
          
      armpickup.setTargetPosition(armcorrection1 + armcorrection2);
      armpickup.setPower(-0.4);
      armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        } 
        if (gamepad1.dpad_right) {
          armpickup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      armpickup.setTargetPosition(150);
      armpickup.setPower(0.4);
      armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        } 
        
        if (gamepad1.dpad_left) {
          armpickup.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      armpickup.setTargetPosition(-150);
      armpickup.setPower(0.4);
      armpickup.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        } 
        
        if (gamepad1.y) {
          conveyor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
          conveyor.setTargetPosition(-9000);
          conveyor.setPower(1);
          conveyor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
        if (gamepad1.a) {
          armservo.setPosition(1);
        }
        if (gamepad1.b){
          armservo.setPosition(0);
        }
        if (gamepad1.x) {
          intake.setPower(0.6);
        } else {
          intake.setPower(0);
        }
        if (gamepad1.dpad_right){
          motorl.setPower(0.8);
        }
       
        telemetry.addData("Armpickup Encoder ticks", armpickup.getCurrentPosition());
        telemetry.update();
         
        
        
        
      }
    }
  }
}
