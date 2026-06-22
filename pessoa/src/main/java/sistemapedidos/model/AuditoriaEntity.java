package sistemapedidos.model;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.FetchType;
import jakarta.persistence.EntityListeners;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditoriaEntity {

	@CreatedDate
	@Column(name = "criado_em", nullable = false)
	private Instant criadoEm;

	@LastModifiedDate
	@Column(name = "atualizado_em", nullable = false)
	private Instant atualizadoEm;

	@CreatedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "criado_por_usuario_id")
	private Usuario criadoPor;

	@LastModifiedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "atualizado_por_usuario_id")
	private Usuario atualizadoPor;

	public Instant getCriadoEm() {
		return criadoEm;
	}

	public Instant getAtualizadoEm() {
		return atualizadoEm;
	}

	public Usuario getCriadoPor() {
		return criadoPor;
	}

	public Usuario getAtualizadoPor() {
		return atualizadoPor;
	}
}
