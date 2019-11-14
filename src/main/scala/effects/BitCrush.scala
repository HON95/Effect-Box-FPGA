package EffectBox

import chisel3._
import chisel3.MultiIOModule

class BitCrushControl extends Bundle {
  val nCrushBits = Input(UInt(4.W))
  val bypass = Input(Bool())
}

class BitCrush extends MultiIOModule {
  val io = IO(new EffectBundle)
  val ctrl = IO(new BitCrushControl)

  io.in.ready := true.B
  io.out.valid := io.in.valid

  // Rightmost 0xD for soft transition
  val mask = 0xFFFFFFFDL.U(32.W) << ctrl.nCrushBits

  when (!ctrl.bypass) {
    // Truncate "towards" signed zero
    when (io.in.bits >= 0.S) {
      io.out.bits := (io.in.bits.asUInt() & mask).asSInt()
    } .otherwise {
      io.out.bits := (io.in.bits.asUInt() | ~mask).asSInt()
    }
  } .otherwise {
    io.out.bits := io.in.bits
  }
}
