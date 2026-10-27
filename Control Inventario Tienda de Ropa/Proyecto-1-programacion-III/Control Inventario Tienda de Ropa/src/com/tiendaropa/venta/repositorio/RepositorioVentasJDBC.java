        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar venta por factura", e);
        }
    }

    @Override
    public List<Venta> obtenerTodas()