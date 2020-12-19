package vexriscv.demo

import vexriscv.plugin._
import vexriscv.{VexRiscv, VexRiscvConfig, plugin}
import spinal.core._
import vexriscv.ip.{DataCacheConfig, InstructionCacheConfig}

/**
 * Created by spinalvm on 15.06.17.
 */
// Based on GenSmallAndProductiveICache.
object GenOrangeSoCLinux extends App{
  def cpu() = new VexRiscv(
    config = VexRiscvConfig(
      plugins = List(
        new IBusCachedPlugin(
          resetVector = 0xff010000l,
          prediction = STATIC,
          compressedGen = true,
          config = InstructionCacheConfig(
            cacheSize = 4096,
            bytePerLine = 32,
            wayCount = 1,
            addressWidth = 32,
            cpuDataWidth = 32,
            memDataWidth = 32,
            catchIllegalAccess = false,
            catchAccessFault = false,
            asyncTagMemory = false,
            twoCycleRam = false,
            twoCycleCache = false
          ),
          memoryTranslatorPortConfig = MmuPortConfig(
            portTlbSize = 2
          )
        ),
        new DBusSimplePlugin(
          catchAddressMisaligned = false,
          catchAccessFault = false,
          earlyInjection = true,
          memoryTranslatorPortConfig = MmuPortConfig(
            portTlbSize = 2
          )
        ),
        new CsrPlugin(CsrPluginConfig(
          // based on "smallest"
          catchIllegalAccess = true,
          mvendorid           = 1,
          marchid             = 2,
          mimpid              = 3,
          mhartid             = 0,
          misaExtensionsInit  = 0,
          misaAccess     = CsrAccess.NONE,
          mtvecAccess    = CsrAccess.WRITE_ONLY,
          mtvecInit      = 0xff010010l,
          mepcAccess     = CsrAccess.READ_WRITE,
          mscratchGen    = false,
          mcauseAccess   = CsrAccess.READ_ONLY,
          mbadaddrAccess = CsrAccess.NONE,
          mcycleAccess   = CsrAccess.NONE,
          minstretAccess = CsrAccess.NONE,
          ecallGen       = true,
          wfiGenAsWait   = false,
          wfiGenAsNop    = true,
          ucycleAccess   = CsrAccess.NONE,
          uinstretAccess = CsrAccess.NONE,
          ebreakGen           = true,
          userGen = true,

          supervisorGen       = true,
          sscratchGen         = true,
          stvecAccess         = CsrAccess.READ_WRITE,
          sepcAccess          = CsrAccess.READ_WRITE,
          scauseAccess        = CsrAccess.READ_WRITE,
          sbadaddrAccess      = CsrAccess.READ_WRITE,
          scycleAccess        = CsrAccess.NONE,
          sinstretAccess      = CsrAccess.NONE,
          satpAccess          = CsrAccess.NONE, //Implemented into the MMU plugin
          medelegAccess       = CsrAccess.WRITE_ONLY,
          midelegAccess       = CsrAccess.WRITE_ONLY
        )),
        new MmuPlugin(
          ioRange = (x => x(31 downto 24) === 0xfe)
        ),
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
