package vexriscv.demo

import vexriscv.plugin._
import vexriscv.{VexRiscv, VexRiscvConfig, plugin}
import spinal.core._
import vexriscv.ip.{DataCacheConfig, InstructionCacheConfig}

/**
 * Created by spinalvm on 15.06.17.
 */
// Based on GenSmallAndProductiveICache.
object GenOrangeSoC extends App{
  def cpu() = new VexRiscv(
    config = VexRiscvConfig(
      plugins = List(
        new IBusCachedPlugin(
          resetVector = 0xff010000l,
          prediction = STATIC,
          compressedGen = true,
          injectorStage = true,
          config = InstructionCacheConfig(
            cacheSize = 6144,
            bytePerLine = 32,
            wayCount = 3,
            addressWidth = 32,
            cpuDataWidth = 32,
            memDataWidth = 32,
            catchIllegalAccess = false,
            catchAccessFault = false,
            asyncTagMemory = false,
            twoCycleRam = false,
            twoCycleCache = true
          )
        ),
        new DBusSimplePlugin(
          catchAddressMisaligned = false,
          catchAccessFault = false,
          earlyInjection = true
        ),
        new CsrPlugin(CsrPluginConfig(
          // based on "smallest"
          catchIllegalAccess = false,
          mvendorid      = null,
          marchid        = null,
          mimpid         = null,
          mhartid        = null,
          misaExtensionsInit = 66,
          misaAccess     = CsrAccess.NONE,
          mtvecAccess    = CsrAccess.READ_WRITE,
          mtvecInit      = 0xff010010l,
          mepcAccess     = CsrAccess.READ_WRITE,
          mscratchGen    = false,
          mcauseAccess   = CsrAccess.READ_ONLY,
          mbadaddrAccess = CsrAccess.NONE,
          mcycleAccess   = CsrAccess.NONE,
          minstretAccess = CsrAccess.NONE,
          ecallGen       = false,
          wfiGenAsWait   = false,
          wfiGenAsNop    = true,
          ucycleAccess   = CsrAccess.NONE,
          uinstretAccess = CsrAccess.NONE
        )),
        new DecoderSimplePlugin(
          catchIllegalInstruction = true
        ),
        new RegFilePlugin(
          regFileReadyKind = plugin.SYNC,
          zeroBoot = false
        ),
        new IntAluPlugin,
        new SrcPlugin(
          separatedAddSub = false,
          executeInsertion = true
        ),
        new FullBarrelShifterPlugin,
        new HazardSimplePlugin(
          bypassExecute           = true,
          bypassMemory            = true,
          bypassWriteBack         = true,
          bypassWriteBackBuffer   = true,
          pessimisticUseSrc       = false,
          pessimisticWriteRegFile = false,
          pessimisticAddressMatch = false
        ),
        new Mul16Plugin(),
        new BranchPlugin(
          earlyBranch = false,
          catchAddressMisaligned = false
        ),
        new YamlPlugin("cpu0.yaml")
      )
    )
  )

  SpinalVerilog(cpu())
}
