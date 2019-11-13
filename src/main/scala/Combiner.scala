package EffectBox

import chisel3._


class Combiner(bitWidth: Int) extends Module {
  val io = IO(
    new Bundle {
      val in = Input(SInt(bitWidth.W))
      val out = Output(SInt(bitWidth.W))
      val n = Input(Bool()) // does nothing
    }
  )
  
  val bitCrush = Module(new BitCrush)
  val delay = Module(new DelayFilter(bitWidth)).io
  delay.bypass := false.B
  bitCrush.ctrl.bypass := false.B
  bitCrush.ctrl.nCrushBits := 4.U

//  delay.in := io.in
//  bitCrush.dataIn := delay.out
//  io.out := bitCrush.dataOut

  bitCrush.io.dataIn := io.in
  delay.in := bitCrush.io.dataOut
  io.out := delay.out
}
