import React, { useEffect, useState, useRef } from 'react';
import { 
  QrCode, 
  UserCheck, 
  UserX, 
  ShieldCheck, 
  ShieldAlert, 
  Search,
  Camera,
  RefreshCw,
  XCircle,
  CheckCircle2,
  Clock
} from 'lucide-react';
import { Html5Qrcode } from "html5-qrcode";
import { accessService } from '../services/access.service';
import { ValidateAccessResponse } from '../types/access.types';
import { Button } from '@/shared/components/Button';
import { Input } from '@/shared/components/Input';

/**
 * AccessValidationPage: Interfaz de recepción para validar accesos por QR o DNI.
 * Sigue el diseño KINETIC: Alto contraste, tipografía agresiva y feedback instantáneo.
 */
export const AccessValidationPage = () => {
  const [result, setResult] = useState<ValidateAccessResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [manualId, setManualId] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isScannerActive, setIsScannerActive] = useState(true);
  
  const html5QrCodeRef = useRef<Html5Qrcode | null>(null);
  const isTransitioning = useRef(false);

  useEffect(() => {
    // Inicializar la instancia solo una vez
    if (!html5QrCodeRef.current) {
      html5QrCodeRef.current = new Html5Qrcode("reader");
    }

    const manageScanner = async () => {
      if (isTransitioning.current || !html5QrCodeRef.current) return;

      if (isScannerActive && !result) {
        if (!html5QrCodeRef.current.isScanning) {
          await startScanner();
        }
      } else {
        if (html5QrCodeRef.current.isScanning) {
          await stopScanner();
        }
      }
    };

    manageScanner();

    return () => {
      // Al desmontar, intentamos detener si está escaneando
      if (html5QrCodeRef.current?.isScanning && !isTransitioning.current) {
        isTransitioning.current = true;
        html5QrCodeRef.current.stop()
          .catch(console.error)
          .finally(() => { isTransitioning.current = false; });
      }
    };
  }, [isScannerActive, result]);

  const startScanner = async () => {
    if (!html5QrCodeRef.current || isTransitioning.current) return;
    
    isTransitioning.current = true;
    try {
      // Simplificamos configuración para máxima compatibilidad
      const config = { 
        fps: 20, 
        aspectRatio: 1.0,
        experimentalFeatures: {
          useBarCodeDetectorIfSupported: true
        }
      };

      await html5QrCodeRef.current.start(
        { facingMode: "environment" }, 
        config,
        (decodedText) => {
          console.log("¡QR Detectado!", decodedText);
          handleValidation(decodedText, 'QR');
          setIsScannerActive(false);
        },
        () => {} 
      );
    } catch (err) {
      console.error("Error al iniciar cámara:", err);
      // Fallback automático si falla environment
      try {
        await html5QrCodeRef.current.start(
          { facingMode: "user" },
          { fps: 20, aspectRatio: 1.0 },
          (decodedText) => {
            console.log("¡QR Detectado!", decodedText);
            handleValidation(decodedText, 'QR');
            setIsScannerActive(false);
          },
          () => {}
        );
      } catch (secondErr) {
        setError("No se pudo acceder a la cámara o los permisos fueron denegados.");
      }
    } finally {
      isTransitioning.current = false;
    }
  };

  const stopScanner = async () => {
    if (!html5QrCodeRef.current || isTransitioning.current) return;
    try {
      if (html5QrCodeRef.current.isScanning) {
        isTransitioning.current = true;
        await html5QrCodeRef.current.stop();
      }
    } catch (err) {
      console.error("Error al detener cámara:", err);
    } finally {
      isTransitioning.current = false;
    }
  };

  const handleValidation = async (identifier: string, method: 'QR' | 'DNI') => {
    // Si es un QR con URL o JSON, limpiamos para quedarnos solo con el token
    let cleanId = identifier;
    try {
      // Si el QR tiene formato JSON o URL, intentamos extraer el token
      if (identifier.includes('token=')) {
        cleanId = identifier.split('token=')[1].split('&')[0];
      }
    } catch (e) {}

    setLoading(true);
    setError(null);
    try {
      const response = await accessService.validateAccess({ identifier: cleanId, method });
      setResult(response);
      
      // Auto-reset después de 3 segundos para volver al scanner
      if (response.accessGranted) {
        setTimeout(() => {
          resetAll();
        }, 3000);
      }
    } catch (err: any) {
      console.error("Error en validación de acceso:", err);
      setError(err.response?.data?.message || 'Error al validar acceso');
      setResult({
        accessGranted: false,
        memberName: 'Error',
        message: 'No se pudo conectar con el servidor'
      });
    } finally {
      setLoading(false);
    }
  };

  const resetAll = () => {
    setResult(null);
    setError(null);
    setManualId('');
    setIsScannerActive(true);
  };

  return (
    <div className="animate-in fade-in slide-in-from-bottom-4 duration-700 pb-10 max-w-5xl mx-auto">
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-12">
        <div>
          <h2 className="text-sm font-sans font-bold text-primary uppercase tracking-[0.4em] mb-3 text-center md:text-left">Recepción</h2>
          <h1 className="text-5xl font-display font-black text-text-main tracking-tight italic text-center md:text-left">
            Control de <span className="text-primary-dark">Accesos</span>.
          </h1>
        </div>
        
        <div className="flex items-center gap-3 bg-surface-low p-2 rounded-2xl border border-white/5 mx-auto md:mx-0">
          <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center text-primary">
            <Camera size={20} />
          </div>
          <div className="pr-4 text-left">
            <p className="text-[10px] font-black uppercase tracking-widest text-text-secondary leading-none mb-1">Cámara</p>
            <p className="text-xs font-bold text-green-400 flex items-center gap-1.5">
              <span className="w-1.5 h-1.5 rounded-full bg-green-400 animate-pulse" />
              Lista para Escanear
            </p>
          </div>
        </div>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 items-start">
        
        {/* PANEL DE ESCANEO / RESULTADO */}
        <section className="relative group">
          <div className={`
            aspect-square rounded-[2.5rem] overflow-hidden border-2 transition-all duration-500 bg-surface-low flex flex-col items-center justify-center relative
            ${result ? (result.accessGranted ? 'border-green-500/40 shadow-[0_0_50px_rgba(34,197,94,0.15)]' : 'border-error/40 shadow-[0_0_50px_rgba(239,68,68,0.15)]') : 'border-white/5'}
          `}>
            
            {/* SCANNER OVERLAY */}
            <div 
              id="reader" 
              className={`w-full h-full overflow-hidden [&_video]:object-contain [&_video]:w-full [&_video]:h-full ${result ? 'hidden' : 'block'}`}
            ></div>

            {/* RESULT VIEW */}
            {result && (
              <div className="absolute inset-0 flex flex-col items-center justify-center p-8 text-center animate-in zoom-in-95 duration-300 bg-surface-low z-20">
                <div className={`
                  w-24 h-24 rounded-3xl flex items-center justify-center mb-6 shadow-2xl transition-transform duration-500 scale-110
                  ${result.accessGranted ? 'bg-green-500 text-black' : 'bg-error text-white'}
                `}>
                  {result.accessGranted ? <UserCheck size={48} strokeWidth={2.5} /> : <UserX size={48} strokeWidth={2.5} />}
                </div>
                
                <h2 className={`text-4xl font-display font-black italic tracking-tight mb-2 ${result.accessGranted ? 'text-green-400' : 'text-error'}`}>
                  {result.accessGranted ? 'ACCESO PERMITIDO' : 'ACCESO DENEGADO'}
                </h2>
                
                <p className="text-2xl font-bold text-text-main mb-6 uppercase tracking-wide">
                  {result.memberName}
                </p>

                <div className="bg-white/5 backdrop-blur-md rounded-2xl p-4 border border-white/5 w-full max-w-sm">
                  <p className="text-sm font-medium text-text-secondary italic">
                    "{result.message}"
                  </p>
                </div>

                <Button 
                  onClick={resetAll}
                  className="mt-8 px-8 py-3 rounded-2xl bg-white/10 hover:bg-white/20 text-white font-black uppercase tracking-widest text-xs transition-all"
                >
                  <RefreshCw size={16} className="mr-2" /> Volver a Escanear
                </Button>
              </div>
            )}

            {/* SCANNING INDICATOR & SCAN LINE */}
            {!result && isScannerActive && (
              <>
                <div className="absolute top-6 left-1/2 -translate-x-1/2 pointer-events-none z-10 flex flex-col items-center gap-2">
                   <div className="px-4 py-1.5 rounded-full bg-primary/20 backdrop-blur-md border border-primary/30 text-primary text-[10px] font-black uppercase tracking-[0.2em] animate-pulse">
                      Escaneando QR...
                   </div>
                </div>
                {/* LÍNEA DE ESCANEO LÁSER */}
                <div className="absolute top-0 left-0 w-full h-[2px] bg-primary shadow-[0_0_15px_rgba(137,172,255,1)] animate-scanLine z-10 pointer-events-none" />
              </>
            )}
          </div>
          
          {/* DECORATIVE CORNERS */}
          {!result && (
            <>
              <div className="absolute -top-1 -left-1 w-12 h-12 border-t-4 border-l-4 border-primary rounded-tl-3xl opacity-40 group-hover:opacity-100 transition-opacity z-10" />
              <div className="absolute -top-1 -right-1 w-12 h-12 border-t-4 border-r-4 border-primary rounded-tr-3xl opacity-40 group-hover:opacity-100 transition-opacity z-10" />
              <div className="absolute -bottom-1 -left-1 w-12 h-12 border-b-4 border-l-4 border-primary rounded-bl-3xl opacity-40 group-hover:opacity-100 transition-opacity z-10" />
              <div className="absolute -bottom-1 -right-1 w-12 h-12 border-b-4 border-r-4 border-primary rounded-br-3xl opacity-40 group-hover:opacity-100 transition-opacity z-10" />
            </>
          )}
        </section>

        {/* PANEL MANUAL / AYUDA */}
        <div className="flex flex-col gap-6">
          <div className="bg-surface-low rounded-[2rem] p-8 border border-white/5 shadow-xl">
            <h3 className="text-xl font-display font-black text-text-main italic mb-6 flex items-center gap-3">
              <Search className="text-primary" size={24} /> Validación Manual
            </h3>
            
            <p className="text-sm text-text-secondary mb-8 leading-relaxed">
              Si el socio tiene problemas con su código QR, ingresa su 
              <span className="text-text-main font-bold"> DNI o Identificador </span> 
              para validar su estado manualmente.
            </p>

            <form 
              onSubmit={(e) => {
                e.preventDefault();
                if (manualId) handleValidation(manualId, 'DNI');
              }}
              className="space-y-4"
            >
              <div className="relative group">
                <Input
                  placeholder="DNI del socio..."
                  value={manualId}
                  onChange={(e) => setManualId(e.target.value)}
                  className="pl-12 h-16 bg-surface-med/50 border-white/5 group-hover:border-primary/30 transition-all rounded-2xl text-lg font-bold"
                />
                <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-text-secondary group-focus-within:text-primary transition-colors" size={20} />
              </div>

              <Button 
                type="submit"
                disabled={loading || !manualId}
                className="w-full py-5 rounded-2xl font-black uppercase tracking-[0.2em] italic text-sm group"
              >
                {loading ? <RefreshCw className="animate-spin mr-2" /> : <ShieldCheck className="mr-2 group-hover:scale-110 transition-transform" />}
                Validar Acceso
              </Button>
            </form>
          </div>

          <div className="bg-surface-low rounded-[2rem] p-8 border border-white/5 shadow-xl flex-1">
             <h3 className="text-sm font-black uppercase tracking-widest text-text-secondary mb-6 flex items-center gap-2">
               <Clock size={16} className="text-primary" /> Instrucciones Rápidas
             </h3>
             
             <div className="space-y-4">
                <div className="flex gap-4 p-4 rounded-2xl bg-surface-high/50 border border-white/[0.02]">
                  <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary shrink-0 font-bold text-xs italic">01</div>
                  <p className="text-xs text-text-secondary leading-relaxed">Pide al socio que muestre su código QR desde su aplicación móvil.</p>
                </div>
                <div className="flex gap-4 p-4 rounded-2xl bg-surface-high/50 border border-white/[0.02]">
                  <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary shrink-0 font-bold text-xs italic">02</div>
                  <p className="text-xs text-text-secondary leading-relaxed">Asegúrate de que el código esté centrado en el visor de la cámara.</p>
                </div>
                <div className="flex gap-4 p-4 rounded-2xl bg-surface-high/50 border border-white/[0.02]">
                  <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary shrink-0 font-bold text-xs italic">03</div>
                  <p className="text-xs text-text-secondary leading-relaxed">El sistema validará automáticamente la membresía y mostrará el estado.</p>
                </div>
             </div>
          </div>
        </div>

      </div>
      
      {/* GLOBAL ERROR (IF ANY) */}
      {error && !result && (
        <div className="mt-8 p-4 rounded-2xl bg-error/10 border border-error/20 text-error text-center font-bold text-sm animate-in fade-in zoom-in-95 duration-300">
           <XCircle className="inline mr-2" size={18} /> {error}
        </div>
      )}
    </div>
  );
};
