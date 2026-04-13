/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_URL: string
  // Agrega aquí otras variables de entorno si las usas...
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
