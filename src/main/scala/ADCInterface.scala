package EffectBox

import chisel3._
import chisel3.util.{Counter}

class ADCInterface extends Module {
  val io = IO(
    new Bundle {
      val bit = Input(UInt(1.W)) // ADC DOUT
      val LRCLK = Input(Bool()) // ADC LRCIN (from FPGA)

      val sample = Output(SInt(16.W))
      val enable = Output(Bool())
    }
  )

  val accumulator = RegInit(UInt(16.W), 0.U)

  io.sample := accumulator.do_asSInt
  io.enable := false.B

  when(io.LRCLK) { // LRCLK high
    // Left shift and add bit
    accumulator := (accumulator << 1) + io.bit
  }.elsewhen(FallingEdge(io.LRCLK)) { // LRCLK falling edge
    io.enable := true.B
  }.otherwise { // LRCLK low, was not previously high
  }
}