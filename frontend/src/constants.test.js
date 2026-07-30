import { describe, it, expect } from 'vitest'
import { STATUS_LABELS } from './constants'

describe('STATUS_LABELS', () => {
  it('traduce los estados del job', () => {
    expect(STATUS_LABELS.PENDIENTE).toBe('Pendiente')
    expect(STATUS_LABELS.PROCESANDO).toBe('Procesando')
    expect(STATUS_LABELS.COMPLETADO).toBe('Completado')
    expect(STATUS_LABELS.ERROR).toBe('Error')
  })
})
