// SPDX-License-Identifier: Apache-2.0

package chiseltest.experimental

import chiseltest._
import chiseltest.internal.{TreadleBackendAnnotation, VerilatorBackendAnnotation}
import chisel3._
import treadle.HasTreadleSuite
import firrtl.stage.{CompilerAnnotation, RunFirrtlTransformAnnotation}
import firrtl.{
  AnnotationSeq,
  ExecutionOptionsManager,
  FirrtlEmitter,
  LowFirrtlCompiler,
  MinimumVerilogCompiler,
  NoneCompiler,
  SystemVerilogCompiler,
  VerilogCompiler,
  VerilogEmitter
}

package object TestOptionBuilder {
  implicit class ChiselScalatestOptionBuilder[T <: MultiIOModule](x: ChiselScalatestTester#TestBuilder[T]) {
    def withAnnotations(annotationSeq: AnnotationSeq): ChiselScalatestTester#TestBuilder[T] = {
      new x.outer.TestBuilder[T](x.dutGen, x.annotationSeq ++ annotationSeq, x.flags)
    }

    def withFlags(flags: Array[String]): ChiselScalatestTester#TestBuilder[T] = {
      new x.outer.TestBuilder[T](x.dutGen, x.annotationSeq, x.flags ++ flags)
    }

    @deprecated("Use withAnnotations instead", "a long time ago")
    def withExecOptions(manager: ExecutionOptionsManager with HasTreadleSuite): ChiselScalatestTester#TestBuilder[T] = {
      val annos = manager.toAnnotationSeq.map {
        case CompilerAnnotation(compiler) if compiler.isInstanceOf[LowFirrtlCompiler]      => TreadleBackendAnnotation
        case CompilerAnnotation(compiler) if compiler.isInstanceOf[NoneCompiler]           => TreadleBackendAnnotation
        case CompilerAnnotation(compiler) if compiler.isInstanceOf[VerilogCompiler]        => VerilatorBackendAnnotation
        case CompilerAnnotation(compiler) if compiler.isInstanceOf[MinimumVerilogCompiler] => VerilatorBackendAnnotation
        case CompilerAnnotation(compiler) if compiler.isInstanceOf[SystemVerilogCompiler]  => VerilatorBackendAnnotation
        case RunFirrtlTransformAnnotation(_: FirrtlEmitter) => TreadleBackendAnnotation
        case RunFirrtlTransformAnnotation(_: VerilogEmitter) => VerilatorBackendAnnotation
        case anno => anno
      }

      new x.outer.TestBuilder[T](x.dutGen, annos, Array.empty)
    }

    @deprecated("Use withAnnotations instead", "a long time ago")
    def withTesterOptions(opt: TesterOptions): ChiselScalatestTester#TestBuilder[T] = {
      new x.outer.TestBuilder[T](x.dutGen, opt.toAnnotations, Array.empty)
    }
  }
}
